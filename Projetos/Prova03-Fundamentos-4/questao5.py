"""
Questao 5 - Algoritmo de Newton para
    minimizar f(x) = (1/2)(x1 - 3)^2 + (x2 - 1)^2
    x0 = [1, 0]^T, precisao 1e-6
    Busca unidirecional: secao aurea com precisao 1e-4 em [0, 2].

Observacao: o algoritmo de Newton "puro" (passo t = 1) converge em
uma unica iteracao, pois a funcao e quadratica.
"""

import math


def f(x):
    return 0.5 * (x[0] - 3.0) ** 2 + (x[1] - 1.0) ** 2


def grad_f(x):
    return [x[0] - 3.0, 2.0 * (x[1] - 1.0)]


def hess_f(x):
    # Hessiana constante (forma quadratica): diag(1, 2)
    return [[1.0, 0.0], [0.0, 2.0]]


def resolve_2x2(H, b):
    """Resolve H d = b para matriz 2x2."""
    a, c = H[0]
    e, g = H[1]
    det = a * g - c * e
    d0 = (b[0] * g - c * b[1]) / det
    d1 = (a * b[1] - e * b[0]) / det
    return [d0, d1]


def norma(v):
    return math.sqrt(sum(vi * vi for vi in v))


def secao_aurea(phi, a, b, tol=1e-4, max_iter=1000):
    r = (math.sqrt(5.0) - 1.0) / 2.0
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


def newton(x0, tol=1e-6, max_iter=1000, puro=False):
    x = list(x0)
    it = 0
    while it < max_iter:
        g = grad_f(x)
        if norma(g) < tol:
            break
        H = hess_f(x)
        # Direcao de Newton: d = -H^{-1} g
        d = resolve_2x2(H, [-g[0], -g[1]])

        if puro:
            t = 1.0
        else:
            phi = lambda t: f([x[0] + t * d[0], x[1] + t * d[1]])
            t = secao_aurea(phi, 0.0, 2.0, tol=1e-4)

        x = [x[0] + t * d[0], x[1] + t * d[1]]
        it += 1
        print(f"  it={it}  x=[{x[0]:.10f}, {x[1]:.10f}]  f(x)={f(x):.3e}  t={t:.6f}")
    return x, it


if __name__ == "__main__":
    x0 = [1.0, 0.0]

    print("=== Newton com busca exata (secao aurea) ===")
    xopt, k = newton(x0, tol=1e-6)
    print(f"x* ~= [{xopt[0]:.10f}, {xopt[1]:.10f}]")
    print(f"f(x*) = {f(xopt):.3e}")
    print(f"Iteracoes: {k}")

    print("\n=== Newton puro (passo fixo t = 1) ===")
    xopt2, k2 = newton(x0, tol=1e-6, puro=True)
    print(f"x* = [{xopt2[0]:.10f}, {xopt2[1]:.10f}]")
    print(f"f(x*) = {f(xopt2):.3e}")
    print(f"Iteracoes: {k2}  (esperado: 1, pois f e quadratica)")
