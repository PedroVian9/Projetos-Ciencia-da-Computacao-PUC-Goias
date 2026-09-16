import numpy as np
from scipy.linalg import lu_factor, lu_solve

# 1. Definindo a Matriz A e o vetor b usando o numpy
A = np.array([
    [1, 2, 3, 0, 0, 0, 0],
    [3, 2, 1, 0, 0, 0, 0],
    [1, 0, 0, 0, 0, 0, 0],
    [0, 0, 0, 1, 0, 0, 0],
    [0, 0, 0, 0, 1, 0, 0],
    [0, 0, 0, 0, 0, 1, 0],
    [0, 0, 0, 0, 0, 0, 1]
], dtype=float)

b = np.array([2, 6, 1, 1, 2, 3, 4], dtype=float)

# 2. Decomposição LU (com pivoteamento automático)
# O lu_factor retorna a matriz fatorada e os índices de pivoteamento
lu_e_piv, piv = lu_factor(A)

# 3 e 4. Resolução direta do sistema Ax = b
x = lu_solve((lu_e_piv, piv), b)

# 5. Exibindo o resultado
print("Vetor Solução x:")
for i, valor in enumerate(x):
    print(f"x[{i + 1}] = {round(valor, 4)}")