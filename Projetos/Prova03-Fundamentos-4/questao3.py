"""
Questao 3 - Algoritmo 6.1 (Derivada Numerica)
Aproximar f'(x) para f(x) = x^x no ponto x = 0.0001
usando diferencas finitas centrais com h sendo sucessivamente reduzido.
"""

import math


def f(x):
    return x ** x  # x^x = exp(x * ln x), valido para x > 0


def derivada_central(f, x, h):
    return (f(x + h) - f(x - h)) / (2.0 * h)


def algoritmo_6_1(f, x, h0=None, fator=0.5, tol=1e-8, max_iter=60):
    # h0 deve ser menor que x para que x - h > 0 (dominio de x^x).
    if h0 is None:
        h0 = x / 2.0
    """
    Algoritmo 6.1: aproximacao da derivada por sucessivas reducoes de h.
    Para quando |D(h) - D(h_anterior)| < tol.
    """
    h = h0
    d_ant = derivada_central(f, x, h)
    print(f"{'iter':>4} {'h':>14} {'D(h)':>20}")
    print(f"{0:>4} {h:>14.6e} {d_ant:>20.10f}")
    for k in range(1, max_iter + 1):
        h *= fator
        d = derivada_central(f, x, h)
        print(f"{k:>4} {h:>14.6e} {d:>20.10f}")
        if abs(d - d_ant) < tol:
            return d, k, h
        d_ant = d
    return d_ant, max_iter, h


if __name__ == "__main__":
    x0 = 0.0001
    print(f"f(x) = x^x  ;  x = {x0}")
    print(f"f({x0}) = {f(x0):.10f}")

    # Valor exato: f'(x) = x^x * (ln(x) + 1)
    exato = f(x0) * (math.log(x0) + 1.0)
    print(f"Valor exato (analitico): f'({x0}) = {exato:.10f}\n")

    aprox, iters, h_final = algoritmo_6_1(f, x0)
    print(f"\nAproximacao final: f'({x0}) ~= {aprox:.10f}")
    print(f"h final           : {h_final:.6e}")
    print(f"Iteracoes         : {iters}")
    print(f"Erro absoluto     : {abs(aprox - exato):.3e}")
