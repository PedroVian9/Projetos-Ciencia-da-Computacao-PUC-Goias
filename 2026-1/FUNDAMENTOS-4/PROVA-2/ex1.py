import math
import cmath

def calcular_raizes(a, b, c):
    if a == 0:
        raise ValueError("O coeficiente 'a' não pode ser zero.")

    delta = b**2 - 4*a*c

    if delta < 0:
        x1 = (-b + cmath.sqrt(delta)) / (2 * a)
        x2 = (-b - cmath.sqrt(delta)) / (2 * a)
        return x1, x2
    else:
        sqrt_delta = math.sqrt(delta)
        
        if b > 0:
            q = -0.5 * (b + sqrt_delta)
        else:
            q = -0.5 * (b - sqrt_delta)

        x1 = q / a
        
        x2 = c / q 
        
        return x1, x2

# BATERIA DE TESTES

print("1. Teste de raízes reais simples (x^2 - 5x + 6 = 0):")
# Esperado: 3 e 2
r1, r2 = calcular_raizes(1, -5, 6)
print(f"Raízes: {r1}, {r2}\n")

print("2. Teste de raízes complexas (x^2 + x + 1 = 0):")
# Esperado: -0.5 + 0.866j e -0.5 - 0.866j
r1, r2 = calcular_raizes(1, 1, 1)
print(f"Raízes: {r1:.3f}, {r2:.3f}\n")

print("3. Teste do caso extremo (10^-8x^2 - 0.8x + 10^-8 = 0):")
# Esperado: ~ 0.8 * 10^8 e ~ 1.25 * 10^-8
a = 10**-8
b = -0.8
c = 10**-8

r1, r2 = calcular_raizes(a, b, c)
 
print(f"Raiz 1: {r1}")
print(f"Raiz 2: {r2}")