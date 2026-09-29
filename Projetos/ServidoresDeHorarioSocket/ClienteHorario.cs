using System.Net;
using System.Net.Sockets;
using System.Text;

namespace ServidoresDeHorarioSocket;

/// <summary>Cliente que consulta os três servidores TCP de horário.</summary>
public sealed class ClienteHorario
{
    private static readonly (string Nome, int Porta)[] Servers =
    [ ("Brasil", 5001), ("França", 5002), ("Estados Unidos", 5003) ];

    public async Task ConsultarTodosAsync()
    {
        Console.WriteLine("Consultando os servidores de horário por socket TCP...\n");
        Task<string>[] consultas = Servers.Select(server => ConsultarAsync(server.Nome, server.Porta)).ToArray();
        foreach (string resposta in await Task.WhenAll(consultas)) Console.WriteLine(resposta);
    }

    private static async Task<string> ConsultarAsync(string nome, int porta)
    {
        using Socket socket = new(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
        try
        {
            using CancellationTokenSource timeout = new(TimeSpan.FromSeconds(5));
            await socket.ConnectAsync(new IPEndPoint(IPAddress.Loopback, porta), timeout.Token);
            await socket.SendAsync(Encoding.UTF8.GetBytes("HORA\n"), SocketFlags.None, timeout.Token);
            byte[] buffer = new byte[256];
            int bytesRead = await socket.ReceiveAsync(buffer, SocketFlags.None, timeout.Token);
            return Encoding.UTF8.GetString(buffer, 0, bytesRead).Trim();
        }
        catch (SocketException exception) { return $"{nome}: servidor indisponível na porta {porta} ({exception.SocketErrorCode})."; }
        catch (OperationCanceledException) { return $"{nome}: tempo de conexão esgotado na porta {porta}."; }
    }
}
