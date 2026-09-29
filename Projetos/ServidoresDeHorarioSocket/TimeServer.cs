using System.Net;
using System.Net.Sockets;
using System.Text;

namespace ServidoresDeHorarioSocket;

/// <summary>Base dos três servidores de hora, implementados com Socket TCP.</summary>
public abstract class TimeServer
{
    private const string ConsultaValida = "HORA";

    protected TimeServer(string pais, string cidade, string timeZoneId, int porta)
        => (Pais, Cidade, TimeZoneId, Porta) = (pais, cidade, timeZoneId, porta);

    protected string Pais { get; }
    protected string Cidade { get; }
    protected string TimeZoneId { get; }
    public int Porta { get; }

    public async Task StartAsync(CancellationToken cancellationToken = default)
    {
        using Socket listener = new(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
        listener.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReuseAddress, true);
        listener.Bind(new IPEndPoint(IPAddress.Loopback, Porta));
        listener.Listen(backlog: 10);

        Console.WriteLine($"Servidor {Pais} ativo em 127.0.0.1:{Porta} ({Cidade}).");
        Console.WriteLine("Aguardando consultas. Pressione Ctrl+C para encerrar.");

        while (!cancellationToken.IsCancellationRequested)
        {
            Socket client;
            try { client = await listener.AcceptAsync(cancellationToken); }
            catch (OperationCanceledException) { break; }
            _ = HandleClientAsync(client, cancellationToken);
        }
    }

    private async Task HandleClientAsync(Socket client, CancellationToken cancellationToken)
    {
        using (client)
        {
            try
            {
                byte[] buffer = new byte[256];
                int bytesRead = await client.ReceiveAsync(buffer, SocketFlags.None, cancellationToken);
                string request = Encoding.UTF8.GetString(buffer, 0, bytesRead).Trim();
                string response = request.Equals(ConsultaValida, StringComparison.OrdinalIgnoreCase)
                    ? GetCurrentTimeMessage() : "ERRO: envie o comando HORA.";
                await client.SendAsync(Encoding.UTF8.GetBytes(response + Environment.NewLine), SocketFlags.None, cancellationToken);
                Console.WriteLine($"Consulta de {client.RemoteEndPoint}: {response}");
            }
            catch (SocketException exception) { Console.WriteLine($"Erro de socket: {exception.Message}"); }
            catch (OperationCanceledException) { }
        }
    }

    private string GetCurrentTimeMessage()
    {
        TimeZoneInfo timeZone = TimeZoneInfo.FindSystemTimeZoneById(TimeZoneId);
        DateTimeOffset localTime = TimeZoneInfo.ConvertTime(DateTimeOffset.UtcNow, timeZone);
        return $"{Pais} | {Cidade} | {localTime:dd/MM/yyyy HH:mm:ss} (UTC{localTime:zzz})";
    }
}
