"""
Questao 2 - Algoritmo 5.1 (Reta de Quadrados Minimos)
Ajustar y = a*x + b pelos minimos quadrados aos pontos (xk, yk)
e plotar o grafico dos pontos e da reta.
"""

import matplotlib.pyplot as plt


def minimos_quadrados(x, y):
    """Retorna (a, b) tal que y ~= a*x + b minimiza sum (yk - (a*xk + b))^2."""
    n = len(x)
    sx = sum(x)
    sy = sum(y)
    sxx = sum(xi * xi for xi in x)
    sxy = sum(xi * yi for xi, yi in zip(x, y))

    a = (n * sxy - sx * sy) / (n * sxx - sx * sx)
    b = (sy - a * sx) / n
    return a, b


if __name__ == "__main__":
    x = [-1, 0, 1, 2, 3, 4, 5, 6]
    y = [10, 9, 7, 5, 4, 3, 0, -1]

    a, b = minimos_quadrados(x, y)
    print(f"Reta ajustada: y = {a:.6f} * x + {b:.6f}")

    # Plot
    xr = [min(x) - 0.5, max(x) + 0.5]
    yr = [a * xi + b for xi in xr]

    plt.scatter(x, y, color="red", label="Pontos (xk, yk)")
    plt.plot(xr, yr, color="blue", label=f"y = {a:.3f}x + {b:.3f}")
    plt.title("Reta de Quadrados Minimos")
    plt.xlabel("x")
    plt.ylabel("y")
    plt.legend()
    plt.grid(True)
    plt.savefig("questao2_grafico.png", dpi=120)
    plt.show()
