# Servidores de Horário com Socket (C#)

Trabalho de Redes 2: três servidores TCP informam a hora de seus países e uma classe cliente consulta os três.

O requisito de socket é atendido diretamente com `System.Net.Sockets.Socket`, usando TCP e o protocolo abaixo:

```text
Cliente -> servidor: HORA
Servidor -> cliente: País | Cidade | data hora (UTC±hh:mm)
```

## Classes e portas

- `BrasilTimeServer`: Brasília, porta `5001`.
- `FrancaTimeServer`: Paris, porta `5002`.
- `UsaTimeServer`: Washington, D.C., porta `5003`.
- `ClienteHorario`: conecta-se aos três servidores e mostra as respostas.

Os fusos IANA (`America/Sao_Paulo`, `Europe/Paris` e `America/New_York`) aplicam horário de verão automaticamente.

## Como executar

É necessário .NET 8 SDK ou superior. Abra quatro terminais nesta pasta e rode um comando em cada um:

```powershell
dotnet run -- brasil
dotnet run -- franca
dotnet run -- eua
dotnet run -- cliente
```

Inicie primeiro os três servidores. Eles continuam ativos até `Ctrl+C`; em seguida rode o cliente, que consulta todos em paralelo e encerra.
