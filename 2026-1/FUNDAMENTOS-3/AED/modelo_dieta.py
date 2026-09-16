from scipy.optimize import linprog

# Função objetivo (negada → maximizar)
c = [-15, -18]

# Restrições: proteína, vegetais, tempo
A = [[0.25, 0.10],
     [0.10, 0.20],
     [4,    5]]

b = [50, 40, 900]
bounds = [(0, None), (0, None)]

resultado = linprog(c, A_ub=A, b_ub=b,
                    bounds=bounds, method='highs')

print("Tradicional:", resultado.x[0])
print("Fit:        ", resultado.x[1])
print("Lucro Máx.: ", -resultado.fun)
