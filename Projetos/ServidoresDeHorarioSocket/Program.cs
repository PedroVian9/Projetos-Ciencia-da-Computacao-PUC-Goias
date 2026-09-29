using ServidoresDeHorarioSocket;

if (args.Length == 0)
{
    ShowUsage();
    return;
}

switch (args[0].ToLowerInvariant())
{
    case "brasil": await new BrasilTimeServer().StartAsync(); break;
    case "franca":
    case "frança": await new FrancaTimeServer().StartAsync(); break;
    case "eua":
    case "usa": await new UsaTimeServer().StartAsync(); break;
    case "cliente": await new ClienteHorario().ConsultarTodosAsync(); break;
    default:
        Console.Error.WriteLine($"Comando desconhecido: {args[0]}");
        ShowUsage();
        Environment.ExitCode = 1;
        break;
}

static void ShowUsage()
{
    Console.WriteLine("Uso: dotnet run -- [brasil|franca|eua|cliente]");
}
