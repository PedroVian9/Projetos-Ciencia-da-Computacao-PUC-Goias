"""
Questao 1 - Algoritmo 4.1 (Interpolacao de Lagrange)
Calcular L3(1.8) usando os 4 pontos da tabela mais proximos de x = 1.8.
Tabela:
  x: 0.7  1.2  1.3  1.5  2.0  2.3  2.6
  f: 0.043 1.928 2.497 3.875 9.0 13.467 19.176
"""

def lagrange(xs, fs, x):
    """Calcula o polinomio interpolador de Lagrange de grau len(xs)-1 em x."""
    n = len(xs)
    resultado = 0.0
    for k in range(n):
        Lk = 1.0
        for j in range(n):
            if j != k:
                Lk *= (x - xs[j]) / (xs[k] - xs[j])
        resultado += fs[k] * Lk
    return resultado


if __name__ == "__main__":
    x_tab = [0.7, 1.2, 1.3, 1.5, 2.0, 2.3, 2.6]
    f_tab = [0.043, 1.928, 2.497, 3.875, 9.0, 13.467, 19.176]

    x_alvo = 1.8

    # Selecionar os 4 pontos mais proximos de x_alvo para grau 3 (L3)
    indices = sorted(range(len(x_tab)), key=lambda i: abs(x_tab[i] - x_alvo))[:4]
    indices.sort()

    xs = [x_tab[i] for i in indices]
    fs = [f_tab[i] for i in indices]

    print("Pontos utilizados (L3 - grau 3):")
    for xi, fi in zip(xs, fs):
        print(f"  x = {xi:<5}  f(x) = {fi}")

    valor = lagrange(xs, fs, x_alvo)
    print(f"\nL3({x_alvo}) = {valor:.6f}")
