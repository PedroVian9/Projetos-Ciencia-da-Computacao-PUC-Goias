import numpy as np
import matplotlib.pyplot as plt


def f(x):
    return x**4 - 12 * x**3 + 47 * x**2 - 60 * x


def df(x):
    return 4 * x**3 - 36 * x**2 + 94 * x - 60


def newton_raphson(x0, tol=1e-6, max_iter=100):
    x = float(x0)
    for i in range(max_iter):
        fx = f(x)
        if abs(fx) < tol:
            return x, i

        dfx = df(x)
        if abs(dfx) < 1e-14:
            return None, i

        x = x - fx / dfx

    return x, max_iter


def desenhar_grafico():
    x_vals = np.linspace(-1.5, 6.5, 600)
    y_vals = f(x_vals)

    plt.figure(figsize=(8, 5))
    plt.plot(x_vals, y_vals, color="blue", label="f(x) = x^4 - 12x^3 + 47x^2 - 60x")
    plt.axhline(0, color="black", linewidth=1.2)
    plt.scatter([0, 3, 4, 5], [0, 0, 0, 0], color="red", zorder=5, label="Raizes")
    plt.grid(True, linestyle="--", alpha=0.7)
    plt.title("Grafico da funcao f(x)")
    plt.xlabel("x")
    plt.ylabel("f(x)")
    plt.legend()
    plt.tight_layout()
    plt.show()


def main():
    # (a)
    desenhar_grafico()

    # (b.1) ate (b.4)
    print("Metodo de Newton-Raphson para f(x) = 0:\n")

    pontos_iniciais = {
        "b.1": 0.5,
        "b.2": 1.0,
        "b.3": 2.0,
        "b.4": 3.4556,
    }

    raizes_encontradas = set()

    for item, x0 in pontos_iniciais.items():
        raiz, iteracoes = newton_raphson(x0)
        if raiz is None:
            print(f"({item}) x0 = {x0}: metodo falhou (derivada muito proxima de zero).")
            continue

        raiz_arredondada = round(raiz)
        raizes_encontradas.add(raiz_arredondada)
        print(f"({item}) x0 = {x0}: raiz = {raiz:.6f}, iteracoes = {iteracoes}")

    # (b.5)
    todas_raizes = {0, 3, 4, 5}
    faltantes = sorted(todas_raizes - raizes_encontradas)

    print("\n(b.5)")
    if not faltantes:
        print("Nao faltou nenhuma raiz com os pontos iniciais dados.")
        return

    raiz_faltante = faltantes[0]
    # Para esta funcao, x0 = 4.1 converge para a raiz 4 rapidamente.
    x0_b5 = 4.1
    raiz_b5, iter_b5 = newton_raphson(x0_b5)

    if raiz_b5 is None:
        print(f"Com x0 = {x0_b5}, o metodo falhou.")
        return

    print(f"Raiz que faltava: {raiz_faltante}")
    print(f"x0 escolhido: {x0_b5}")
    print(f"Raiz encontrada: {raiz_b5:.6f}")
    print(f"Numero de iteracoes: {iter_b5}")


if __name__ == "__main__":
    main()
