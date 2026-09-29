namespace ServidoresDeHorarioSocket;

/// <summary>Servidor de horário de Washington, EUA.</summary>
public sealed class UsaTimeServer : TimeServer
{
    public UsaTimeServer() : base("Estados Unidos", "Washington, D.C.", "America/New_York", 5003) { }
}
