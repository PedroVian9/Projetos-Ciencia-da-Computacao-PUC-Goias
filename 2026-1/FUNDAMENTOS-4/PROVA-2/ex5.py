import numpy as np

# Definição do sistema de funções F(x)
def F(x):
    f1 = x[0] + x[1] - 3.0
    f2 = x[0]**2 + x[1]**2 - 9.0
    return np.array([f1, f2])

# Definição da Matriz Jacobiana J(x)
def J(x):
    return np.array([
        [1.0, 1.0],
        [2*x[0], 2*x[1]]
    ])

def newton_sistema(F, J, x0, tol, max_iter=100):
    x = x0.copy()
    
    for k in range(max_iter):
        Fx = F(x)
        Jx = J(x)
        
        # Resolve o sistema linear: J(x) * delta_x = -F(x)
        delta_x = np.linalg.solve(Jx, -Fx)
        
        # Atualiza a estimativa
        x = x + delta_x
        
        # Critério de parada
        if np.linalg.norm(delta_x, np.inf) < tol:
            return x, k + 1
            
    return x, max_iter

# Dados do problema (ponto inicial é o vetor transposto (1, 5)^T)
x0 = np.array([1.0, 5.0])
tolerancia = 1e-5

# Execução
x_res, iteracoes = newton_sistema(F, J, x0, tolerancia)

print(f"Solução encontrada: x = {np.round(x_res, 5)}")
print(f"Número de iterações: {iteracoes}")