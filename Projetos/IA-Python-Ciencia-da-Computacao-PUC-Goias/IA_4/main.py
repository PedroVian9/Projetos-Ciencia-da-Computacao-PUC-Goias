# Pipeline de Classificação de Células Sanguíneas - Malária
# Utilizando PyTorch (Versão Otimizada)
# Disciplina: Inteligência Artificial - AED4

import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, confusion_matrix, roc_curve, auc, classification_report
import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import Dataset, DataLoader, TensorDataset
import torchvision.transforms as transforms
from PIL import Image
import cv2
import warnings
warnings.filterwarnings('ignore')

# Configuração do dispositivo (GPU se disponível, senão CPU)
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
print(f"Usando dispositivo: {device}")

class MalariaDataset(Dataset):
    """Dataset customizado para imagens de malária"""
    
    def __init__(self, data_path, img_size=(64, 64), transform=None):
        self.data_path = data_path
        self.img_size = img_size
        self.transform = transform
        self.images = []
        self.labels = []
        
        self._load_data()
    
    def _load_data(self):
        """Carrega os dados das pastas"""
        # Ajuste do caminho para a estrutura aninhada
        base_path = os.path.join(self.data_path, 'cell_images') if 'cell_images' not in self.data_path else self.data_path
        
        # Caminhos das classes
        parasitized_path = os.path.join(base_path, 'Parasitized')
        uninfected_path = os.path.join(base_path, 'Uninfected')
        
        print(f"Buscando imagens em:")
        print(f"- Parasitized: {parasitized_path}")
        print(f"- Uninfected: {uninfected_path}")
        
        # Função para carregar imagens de uma pasta
        def load_from_folder(folder_path, label):
            if not os.path.exists(folder_path):
                print(f"AVISO: Pasta não encontrada: {folder_path}")
                return
                
            count = 0
            for filename in os.listdir(folder_path):
                if filename.lower().endswith(('.png', '.jpg', '.jpeg')):
                    img_path = os.path.join(folder_path, filename)
                    self.images.append(img_path)
                    self.labels.append(label)
                    count += 1
            print(f"Carregadas {count} imagens de {os.path.basename(folder_path)}")
        
        # Carrega imagens: 0 = Uninfected, 1 = Parasitized
        load_from_folder(uninfected_path, 0)
        load_from_folder(parasitized_path, 1)
        
        print(f"Total de imagens carregadas: {len(self.images)}")
        if len(self.labels) > 0:
            unique, counts = np.unique(self.labels, return_counts=True)
            print(f"Distribuição: Uninfected={counts[0] if 0 in unique else 0}, Parasitized={counts[1] if 1 in unique else 0}")
    
    def __len__(self):
        return len(self.images)
    
    def __getitem__(self, idx):
        # Carrega imagem
        img_path = self.images[idx]
        try:
            image = cv2.imread(img_path)
            if image is None:
                raise ValueError(f"Não foi possível carregar a imagem: {img_path}")
            
            image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
            image = cv2.resize(image, self.img_size)
            
            # Normalização
            image = image.astype(np.float32) / 255.0
            
            # Aplicar transformações se existir
            if self.transform:
                image = self.transform(image)
            else:
                image = torch.from_numpy(image).permute(2, 0, 1)  # HWC -> CHW
            
            label = torch.tensor(self.labels[idx], dtype=torch.float32)
            
            return image, label
        except Exception as e:
            print(f"Erro ao carregar imagem {img_path}: {e}")
            # Retorna uma imagem vazia em caso de erro
            dummy_image = torch.zeros(3, self.img_size[0], self.img_size[1])
            return dummy_image, torch.tensor(0.0)

class MalariaMLPNet(nn.Module):
    """Rede Neural MLP para classificação de malária"""
    
    def __init__(self, input_size, hidden_sizes=[512, 256, 128], dropout_rate=0.3):
        super(MalariaMLPNet, self).__init__()
        
        layers = []
        prev_size = input_size
        
        # Camadas ocultas
        for hidden_size in hidden_sizes:
            layers.extend([
                nn.Linear(prev_size, hidden_size),
                nn.ReLU(),
                nn.Dropout(dropout_rate)
            ])
            prev_size = hidden_size
        
        # Camada de saída
        layers.append(nn.Linear(prev_size, 1))
        layers.append(nn.Sigmoid())
        
        self.network = nn.Sequential(*layers)
        
    def forward(self, x):
        # Flatten da imagem
        x = x.view(x.size(0), -1)
        return self.network(x)

class MalariaClassifierPyTorch:
    """Classificador de malária usando PyTorch"""
    
    def __init__(self, data_path, img_size=(64, 64), batch_size=32):
        self.data_path = data_path
        self.img_size = img_size
        self.batch_size = batch_size
        self.model = None
        self.train_losses = []
        self.val_losses = []
        self.train_accuracies = []
        self.val_accuracies = []
        
    def prepare_data(self, test_size=0.2, val_size=0.2):
        """Prepara os dados para treinamento"""
        print("Preparando dados...")
        
        # Carrega dataset
        dataset = MalariaDataset(self.data_path, self.img_size)
        
        if len(dataset) == 0:
            raise ValueError("Nenhuma imagem foi carregada! Verifique o caminho dos dados.")
        
        # Converte para arrays numpy para divisão
        images = []
        labels = []
        
        print("Convertendo dados...")
        for i in range(len(dataset)):
            img, label = dataset[i]
            images.append(img.numpy())
            labels.append(label.numpy())
        
        X = np.array(images)
        y = np.array(labels)
        
        print(f"Shape das imagens: {X.shape}")
        print(f"Shape dos labels: {y.shape}")
        
        # Verifica se há pelo menos 2 classes
        unique_labels = np.unique(y)
        if len(unique_labels) < 2:
            raise ValueError("É necessário ter pelo menos 2 classes para classificação!")
        
        # Divide os dados
        X_temp, X_test, y_temp, y_test = train_test_split(
            X, y, test_size=test_size, random_state=42, stratify=y
        )
        
        X_train, X_val, y_train, y_val = train_test_split(
            X_temp, y_temp, test_size=val_size/(1-test_size),
            random_state=42, stratify=y_temp
        )
        
        print(f"Treino: {len(X_train)} amostras")
        print(f"Validação: {len(X_val)} amostras")
        print(f"Teste: {len(X_test)} amostras")
        
        # Converte para tensors e cria DataLoaders
        train_dataset = TensorDataset(
            torch.from_numpy(X_train), 
            torch.from_numpy(y_train)
        )
        val_dataset = TensorDataset(
            torch.from_numpy(X_val), 
            torch.from_numpy(y_val)
        )
        test_dataset = TensorDataset(
            torch.from_numpy(X_test), 
            torch.from_numpy(y_test)
        )
        
        self.train_loader = DataLoader(train_dataset, batch_size=self.batch_size, shuffle=True)
        self.val_loader = DataLoader(val_dataset, batch_size=self.batch_size, shuffle=False)
        self.test_loader = DataLoader(test_dataset, batch_size=self.batch_size, shuffle=False)
        
        return X_test, y_test
    
    def create_model(self):
        """Cria o modelo MLP"""
        input_size = 3 * self.img_size[0] * self.img_size[1]  # 3 canais RGB
        self.model = MalariaMLPNet(input_size).to(device)
        
        print("Modelo criado:")
        print(self.model)
        total_params = sum(p.numel() for p in self.model.parameters() if p.requires_grad)
        print(f"Parâmetros treináveis: {total_params:,}")
        
    def train_model(self, epochs=50, learning_rate=0.001):
        """Treina o modelo"""
        print(f"Iniciando treinamento por {epochs} épocas...")
        
        criterion = nn.BCELoss()
        optimizer = optim.Adam(self.model.parameters(), lr=learning_rate, weight_decay=1e-4)
        scheduler = optim.lr_scheduler.ReduceLROnPlateau(optimizer, patience=5, factor=0.5)
        
        best_val_loss = float('inf')
        patience_counter = 0
        
        for epoch in range(epochs):
            # Treinamento
            self.model.train()
            train_loss = 0.0
            train_correct = 0
            train_total = 0
            
            for batch_x, batch_y in self.train_loader:
                batch_x, batch_y = batch_x.to(device), batch_y.to(device)
                
                optimizer.zero_grad()
                outputs = self.model(batch_x).squeeze()
                loss = criterion(outputs, batch_y)
                loss.backward()
                
                # Gradient clipping
                torch.nn.utils.clip_grad_norm_(self.model.parameters(), max_norm=1.0)
                
                optimizer.step()
                
                train_loss += loss.item()
                predicted = (outputs > 0.5).float()
                train_total += batch_y.size(0)
                train_correct += (predicted == batch_y).sum().item()
            
            # Validação
            self.model.eval()
            val_loss = 0.0
            val_correct = 0
            val_total = 0
            
            with torch.no_grad():
                for batch_x, batch_y in self.val_loader:
                    batch_x, batch_y = batch_x.to(device), batch_y.to(device)
                    
                    outputs = self.model(batch_x).squeeze()
                    loss = criterion(outputs, batch_y)
                    
                    val_loss += loss.item()
                    predicted = (outputs > 0.5).float()
                    val_total += batch_y.size(0)
                    val_correct += (predicted == batch_y).sum().item()
            
            # Calcula métricas
            train_loss /= len(self.train_loader)
            val_loss /= len(self.val_loader)
            train_acc = train_correct / train_total
            val_acc = val_correct / val_total
            
            # Salva histórico
            self.train_losses.append(train_loss)
            self.val_losses.append(val_loss)
            self.train_accuracies.append(train_acc)
            self.val_accuracies.append(val_acc)
            
            # Early stopping
            if val_loss < best_val_loss:
                best_val_loss = val_loss
                patience_counter = 0
                torch.save(self.model.state_dict(), 'best_malaria_model.pth')
            else:
                patience_counter += 1
            
            scheduler.step(val_loss)
            
            if (epoch + 1) % 5 == 0 or epoch == 0:
                print(f'Época {epoch+1}/{epochs}: '
                      f'Train Loss: {train_loss:.4f}, Train Acc: {train_acc:.4f}, '
                      f'Val Loss: {val_loss:.4f}, Val Acc: {val_acc:.4f}')
            
            if patience_counter >= 15:  # Aumentei a paciência
                print("Early stopping ativado!")
                break
        
        # Carrega o melhor modelo
        if os.path.exists('best_malaria_model.pth'):
            self.model.load_state_dict(torch.load('best_malaria_model.pth'))
            print("Melhor modelo carregado!")
        print("Treinamento concluído!")
    
    def evaluate_model(self, X_test, y_test):
        """Avalia o modelo"""
        print("Avaliando modelo...")
        
        self.model.eval()
        y_pred_proba = []
        y_pred = []
        
        with torch.no_grad():
            for batch_x, batch_y in self.test_loader:
                batch_x = batch_x.to(device)
                outputs = self.model(batch_x).squeeze()
                
                proba = outputs.cpu().numpy()
                pred = (outputs > 0.5).float().cpu().numpy()
                
                if proba.ndim == 0:  # Caso de um único elemento
                    proba = [proba.item()]
                    pred = [pred.item()]
                
                y_pred_proba.extend(proba)
                y_pred.extend(pred)
        
        y_pred_proba = np.array(y_pred_proba)
        y_pred = np.array(y_pred)
        
        # Métricas
        accuracy = accuracy_score(y_test, y_pred)
        cm = confusion_matrix(y_test, y_pred)
        
        # Verifica se há ambas as classes para calcular ROC
        if len(np.unique(y_test)) > 1:
            fpr, tpr, _ = roc_curve(y_test, y_pred_proba)
            roc_auc = auc(fpr, tpr)
        else:
            fpr, tpr, roc_auc = None, None, 0.0
        
        print(f"Acurácia: {accuracy:.4f}")
        print(f"AUC: {roc_auc:.4f}")
        print("\nRelatório de Classificação:")
        print(classification_report(y_test, y_pred, target_names=['Uninfected', 'Parasitized']))
        
        # Visualizações
        self.plot_results(cm, fpr, tpr, roc_auc, X_test, y_test, y_pred, y_pred_proba, accuracy)
        
        return {
            'accuracy': accuracy,
            'confusion_matrix': cm,
            'roc_auc': roc_auc,
            'classification_report': classification_report(y_test, y_pred, output_dict=True)
        }
    
    def plot_results(self, cm, fpr, tpr, roc_auc, X_test, y_test, y_pred, y_pred_proba, accuracy):
        """Plota os resultados"""
        plt.figure(figsize=(16, 12))
        
        # Matriz de confusão
        plt.subplot(2, 3, 1)
        sns.heatmap(cm, annot=True, fmt='d', cmap='Blues',
                   xticklabels=['Uninfected', 'Parasitized'],
                   yticklabels=['Uninfected', 'Parasitized'])
        plt.title('Matriz de Confusão')
        plt.ylabel('Valor Real')
        plt.xlabel('Predição')
        
        # Curva ROC
        plt.subplot(2, 3, 2)
        if fpr is not None and tpr is not None:
            plt.plot(fpr, tpr, color='darkorange', lw=2, 
                    label=f'ROC curve (AUC = {roc_auc:.2f})')
            plt.plot([0, 1], [0, 1], color='navy', lw=2, linestyle='--')
            plt.xlim([0.0, 1.0])
            plt.ylim([0.0, 1.05])
            plt.xlabel('Taxa de Falsos Positivos')
            plt.ylabel('Taxa de Verdadeiros Positivos')
            plt.title('Curva ROC')
            plt.legend(loc="lower right")
        else:
            plt.text(0.5, 0.5, 'ROC não disponível\n(apenas uma classe)', 
                    ha='center', va='center', transform=plt.gca().transAxes)
            plt.title('Curva ROC')
        
        # Curva de perda
        plt.subplot(2, 3, 3)
        plt.plot(self.train_losses, label='Perda - Treino', color='blue')
        plt.plot(self.val_losses, label='Perda - Validação', color='orange')
        plt.title('Curva de Perda')
        plt.xlabel('Épocas')
        plt.ylabel('Perda')
        plt.legend()
        plt.grid(True, alpha=0.3)
        
        # Curva de acurácia
        plt.subplot(2, 3, 4)
        plt.plot(self.train_accuracies, label='Acurácia - Treino', color='blue')
        plt.plot(self.val_accuracies, label='Acurácia - Validação', color='orange')
        plt.title('Curva de Acurácia')
        plt.xlabel('Épocas')
        plt.ylabel('Acurácia')
        plt.legend()
        plt.grid(True, alpha=0.3)
        
        # Distribuição das probabilidades
        plt.subplot(2, 3, 5)
        if len(np.unique(y_test)) > 1:
            plt.hist(y_pred_proba[y_test == 0], bins=30, alpha=0.7, 
                    label='Uninfected', color='blue', density=True)
            plt.hist(y_pred_proba[y_test == 1], bins=30, alpha=0.7, 
                    label='Parasitized', color='red', density=True)
            plt.xlabel('Probabilidade Predita')
            plt.ylabel('Densidade')
            plt.title('Distribuição das Probabilidades')
            plt.legend()
        else:
            plt.hist(y_pred_proba, bins=30, alpha=0.7, color='blue', density=True)
            plt.xlabel('Probabilidade Predita')
            plt.ylabel('Densidade')
            plt.title('Distribuição das Probabilidades')
        
        # Resumo do modelo
        plt.subplot(2, 3, 6)
        total_params = sum(p.numel() for p in self.model.parameters())
        summary_text = f"""Modelo PyTorch MLP

Acurácia: {accuracy:.4f}
AUC: {roc_auc:.4f}

Arquitetura:
- Input: {3 * self.img_size[0] * self.img_size[1]}
- Hidden: [512, 256, 128]
- Output: 1

Total de parâmetros: {total_params:,}

Dispositivo: {device}
Tamanho da imagem: {self.img_size}
Batch size: {self.batch_size}"""
        
        plt.text(0.1, 0.9, summary_text, fontsize=10, 
                transform=plt.gca().transAxes, verticalalignment='top',
                bbox=dict(boxstyle="round,pad=0.3", facecolor="lightblue", alpha=0.5))
        plt.axis('off')
        plt.title('Resumo do Modelo')
        
        plt.tight_layout()
        plt.show()
    
    def run_complete_pipeline(self):
        """Executa o pipeline completo"""
        print("=== PIPELINE DE CLASSIFICAÇÃO DE MALÁRIA (PyTorch) ===")
        print(f"PyTorch versão: {torch.__version__}")
        print(f"Dispositivo: {device}")
        
        # Lista possíveis caminhos para os dados
        possible_paths = [
            self.data_path,
            os.path.join(self.data_path, 'cell_images'),
            'cell_images',
            'cell_images/cell_images'
        ]
        
        data_found = False
        for path in possible_paths:
            parasitized_path = os.path.join(path, 'Parasitized')
            uninfected_path = os.path.join(path, 'Uninfected')
            
            if os.path.exists(parasitized_path) and os.path.exists(uninfected_path):
                self.data_path = path
                data_found = True
                print(f"Dados encontrados em: {path}")
                break
        
        if not data_found:
            print(f"ERRO: Pastas Parasitized e Uninfected não encontradas!")
            print("Caminhos verificados:")
            for path in possible_paths:
                print(f"  - {path}")
            return None
        
        # Conta imagens
        parasitized_path = os.path.join(self.data_path, 'Parasitized')
        uninfected_path = os.path.join(self.data_path, 'Uninfected')
        
        parasitized_count = len([f for f in os.listdir(parasitized_path) 
                               if f.lower().endswith(('.png', '.jpg', '.jpeg'))])
        uninfected_count = len([f for f in os.listdir(uninfected_path) 
                              if f.lower().endswith(('.png', '.jpg', '.jpeg'))])
        
        print(f"\nImagens encontradas:")
        print(f"- Parasitized: {parasitized_count}")
        print(f"- Uninfected: {uninfected_count}")
        print(f"- Total: {parasitized_count + uninfected_count}")
        
        if parasitized_count == 0 or uninfected_count == 0:
            print("ERRO: É necessário ter imagens em ambas as classes!")
            return None
        
        try:
            # Executa pipeline
            X_test, y_test = self.prepare_data()
            self.create_model()
            self.train_model(epochs=30)  # Reduzido para teste mais rápido
            results = self.evaluate_model(X_test, y_test)
            
            return results
        except Exception as e:
            print(f"ERRO durante a execução: {e}")
            import traceback
            traceback.print_exc()
            return None

# Exemplo de uso
if __name__ == "__main__":
    # Caminho para os dados - ajuste conforme necessário
    DATA_PATH = "cell_images"  # ou "D:/Pedro Repos/aedPythonIA_4/cell_images"
    
    print("=== CLASSIFICADOR DE MALÁRIA - PYTORCH ===")
    print(f"Usando PyTorch versão: {torch.__version__}")
    print(f"Dispositivo: {device}")
    
    # Cria e executa o classificador
    classifier = MalariaClassifierPyTorch(DATA_PATH, img_size=(64, 64), batch_size=32)
    
    results = classifier.run_complete_pipeline()
    if results:
        print(f"\n🎉 PIPELINE EXECUTADO COM SUCESSO!")
        print(f"Acurácia final: {results['accuracy']:.4f}")
        print(f"AUC: {results['roc_auc']:.4f}")
        
        # Salva um resumo dos resultados
        summary = {
            'accuracy': results['accuracy'],
            'auc': results['roc_auc'],
            'confusion_matrix': results['confusion_matrix'].tolist(),
            'device': str(device),
            'model_params': sum(p.numel() for p in classifier.model.parameters())
        }
        
        import json
        with open('malaria_results.json', 'w') as f:
            json.dump(summary, f, indent=2)
        print("Resultados salvos em 'malaria_results.json'")
    else:
        print("❌ Falha na execução do pipeline")