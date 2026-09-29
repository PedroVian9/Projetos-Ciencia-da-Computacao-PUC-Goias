# IA 4 — Detecção de malária em imagens

Projeto de classificação de imagens de células como infectadas ou não infectadas por malária. Inclui o código de treinamento e avaliação, o modelo treinado `best_malaria_model.pth`, os resultados em JSON e uma pequena amostra de imagens em `amostra-imagens/`.

A base completa não é versionada aqui para manter o repositório leve. Para executar o treinamento com todos os dados, obtenha a base de células de malária e coloque-a em `cell_images/cell_images/`, mantendo as pastas `Parasitized/` e `Uninfected/`.

## Execução

```bash
pip install torch torchvision pillow matplotlib scikit-learn
python main.py
```

Execute o comando dentro desta pasta para que o conjunto `cell_images/` seja localizado.
