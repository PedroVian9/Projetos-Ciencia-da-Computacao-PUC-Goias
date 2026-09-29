namespace ServidoresDeHorarioSocket;

/// <summary>Servidor de horário de Paris, França.</summary>
public sealed class FrancaTimeServer : TimeServer
{
    public FrancaTimeServer() : base("França", "Paris", "Europe/Paris", 5002) { }
}
