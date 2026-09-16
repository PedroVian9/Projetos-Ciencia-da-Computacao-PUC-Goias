"""
Questao 4 - Algoritmo de Cauchy (Maxima Descida) para
    minimizar f(x) = (1/2)(x1 - 3)^2 + (x2 - 1)^2
    x0 = [1, 0]^T, precisao 1e-6
    Busca unidirecional: secao aurea com precisao 1e-4 em [0, 2].

Observacao: o algoritmo de Cauchy "puro" (passo fixo igual a 1) diverge
para esse problema. A propria implementacao mostra isso (modo "puro").
"""

import math


def f(x):
    return 0.5 * (x[0] - 3.0) ** 2 + (x[1] - 1.0) ** 2


def grad_f(x):
    # df/dx1 = (x1 - 3)
    # df/dx2 = 2*(x2 - 1)
    return [x[0] - 3.0, 2.0 * (x[1] - 1.0)]


def norma(v):
    return math.sqrt(sum(vi * vi for vi in v))


def secao_aurea(phi, a, b, tol=1e-4, max_iter=1000):
    """Minimiza a funcao unidimensional phi em [a, b] pela secao aurea."""
    r = (math.sqrt(5.0) - 1.0) / 2.0  # ~0.6180339887
    x1 = b - r * (b - a)
    x2 = a + r * (b - a)
    f1 = phi(x1)
    f2 = phi(x2)
    it = 0
    while (b - a) > tol and it < max_iter:
        if f1 < f2:
            b = x2
            x2 = x1
            f2 = f1
            x1 = b - r * (b - a)
            f1 = phi(x1)
        else:
            a = x1
            x1 = x2
            f1 = f2
            x2 = a + r * (b - a)
            f2 = phi(x2)
        it += 1
    return (a + b) / 2.0


def cauchy(x0, tol=1e-6, max_iter=10000, puro=False):
    x = list(x0)
    it = 0
    while it < max_iter:
        g = grad_f(x)
        ng = norma(g)
        if ng < tol:
            break
        d = [-gi for gi in g]  # direcao de maxima descida

        if puro:
            t = 1.0  # passo fixo (Cauchy puro)
        else:
            phi = lambda t: f([x[0] + t * d[0], x[1] + t * d[1]])
            t = secao_aurea(phi, 0.0, 2.0, tol=1e-4)

        x = [x[0] + t * d[0], x[1] + t * d[1]]
        it += 1

        if puro and (norma(x) > 1e8 or math.isnan(x[0])):
            print(f"  [puro] divergiu na iteracao {it}, x = {x}")
            return x, it
    return x, it


if __name__ == "__main__":
    x0 = [1.0, 0.0]

    print("=== Cauchy com busca exata (secao aurea) ===")
    xopt, k = cauchy(x0, tol=1e-6)
    print(f"x* ~= [{xopt[0]:.8f}, {xopt[1]:.8f}]")
    print(f"f(x*) = {f(xopt):.3e}")
    print(f"Iteracoes: {k}")

    print("\n=== Cauchy puro (passo fixo t = 1) ===")
    print("Mostrando as primeiras iteracoes para evidenciar a divergencia:")
    x = [1.0, 0.0]
    print(f"  it=0  x={x}  f(x)={f(x):.6f}")
    for it in range(1, 11):
        g = grad_f(x)
        d = [-gi for gi in g]
        x = [x[0] + 1.0 * d[0], x[1] + 1.0 * d[1]]
        print(f"  it={it}  x=[{x[0]:.4f}, {x[1]:.4f}]  f(x)={f(x):.6f}  |grad|={norma(g):.4f}")

    print("\nExplicacao:")
    print(" A Hessiana de f e diag(1, 2). O passo t = 1 e maior que")
    print(" 2/lambda_max = 2/2 = 1 na coordenada x2, levando ao limite")
    print(" de instabilidade. Na coordenada x2, a iteracao x2 <- x2 - 2(x2-1)")
    print(" da x2_novo - 1 = -(x2 - 1), oscilando indefinidamente. A condicao")
    print(" de convergencia para passo fixo exige t < 2/lambda_max; t=1 nao")
    print(" satisfaz com folga, e o metodo nao converge.")
