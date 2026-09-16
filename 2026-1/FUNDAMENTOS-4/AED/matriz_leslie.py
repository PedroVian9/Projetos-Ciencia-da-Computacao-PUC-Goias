import numpy as np


# Parametros do modelo
f0, f1, f2 = 0.0, 2.0, 1.0   # fecundidade por classe
s0, s1 = 0.7, 0.5            # sobrevivencia por classe

# Matriz de Leslie
L = np.array([
    [f0, f1, f2],
    [s0, 0.0, 0.0],
    [0.0, s1, 0.0],
])

# Autovalores e autovetores
eigenvalues, eigenvectors = np.linalg.eig(L)

# Autovalor dominante
idx = np.argmax(np.abs(eigenvalues))
lam1 = np.real(eigenvalues[idx])

# Distribuicao etaria estavel (autovetor normalizado)
v1 = np.real(eigenvectors[:, idx])
v1 = v1 / v1.sum()

# Simulacao de 20 anos
n = np.array([100, 50, 20], dtype=float)
for t in range(1, 21):
    n = L @ n
    print(f"Ano {t:>2}: total = {n.sum():.1f}")

# Resultados
print(f"\nAutovalor dominante: {lam1:.4f}")
print(f"Crescimento anual:   {(lam1 - 1) * 100:.1f}%")
print(
    "Distribuicao estavel: "
    f"filhotes={v1[0]:.3f} adultos={v1[1]:.3f} velhos={v1[2]:.3f}"
)
