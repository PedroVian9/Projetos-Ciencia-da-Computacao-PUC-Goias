import numpy as np

def jacobi(A, b, x0, tol, max_iter=1000):
    n = len(b)
    x = x0.copy()
    x_new = np.zeros_like(x)
    
    for k in range(max_iter):
        for i in range(n):
            # Somatório dos termos onde j != i
            soma = sum(A[i][j] * x[j] for j in range(n) if j != i)
            x_new[i] = (b[i] - soma) / A[i][i]
            
        # Critério de parada: erro máximo absoluto (Norma Infinito) menor que a tolerância
        if np.linalg.norm(x_new - x, np.inf) < tol:
            return x_new, k + 1
            
        x = x_new.copy()
        
    return x, max_iter

# Dados do problema
A = np.array([
    [10.0,  3.0, -2.0],
    [ 2.0,  8.0, -1.0],
    [ 1.0,  1.0,  5.0]
])
b = np.array([57.0, 20.0, -4.0])

# Ponto inicial x0 = O (Vetor nulo)
x0 = np.array([0.0, 0.0, 0.0])
tolerancia = 1e-5

# Execução
x_res, iteracoes = jacobi(A, b, x0, tolerancia)

print(f"Solução encontrada: x = {np.round(x_res, 5)}")
print(f"Número de iterações: {iteracoes}")