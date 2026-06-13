# ArgoCD GitOps Setup

## 1. Install ArgoCD on k3s

```bash
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

Wait for all pods to be ready:

```bash
kubectl wait --for=condition=Ready pods --all -n argocd --timeout=300s
```

## 2. Access ArgoCD UI

Expose the ArgoCD server (port-forward for quick access):

```bash
kubectl port-forward -n argocd svc/argocd-server 8081:443
```

Get the initial admin password:

```bash
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
```

Open: https://localhost:8081 (username: `admin`)

## 3. Add GitHub Secrets

In your GitHub repo → Settings → Secrets and variables → Actions, add:

| Secret | Value |
|---|---|
| `DOCKER_USERNAME` | `hauet2017` |
| `DOCKER_PASSWORD` | Your Docker Hub password or access token |

## 4. Deploy the ArgoCD Application

```bash
kubectl apply -f argocd/application.yaml
```

Or via ArgoCD CLI:

```bash
argocd app create vn-app \
  --repo https://github.com/ha1999/vn.git \
  --path k8s \
  --dest-server https://kubernetes.default.svc \
  --dest-namespace vn \
  --sync-policy automated \
  --self-heal \
  --auto-prune
```

## 5. Verify

```bash
kubectl get applications -n argocd
argocd app get vn-app
```

## How It Works

1. Push to `main` on GitHub
2. GitHub Actions builds the JAR, builds/pushes Docker image to Docker Hub
3. GitHub Actions updates `k8s/app.yaml` with the new image tag and commits back
4. ArgoCD detects the manifest change in git and syncs to the cluster
