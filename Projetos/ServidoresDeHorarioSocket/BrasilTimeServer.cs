namespace ServidoresDeHorarioSocket;

/// <summary>Servidor de horário de Brasília.</summary>
public sealed class BrasilTimeServer : TimeServer
{
    public BrasilTimeServer() : base("Brasil", "Brasília", "America/Sao_Paulo", 5001) { }
}
