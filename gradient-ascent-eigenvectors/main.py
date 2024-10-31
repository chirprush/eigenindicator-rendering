from math import log, sqrt
import numpy as np

# A = np.array([
#     [0.32, 0.53, 0.69],
#     [0.12, 0.33, 0.54],
#     [0.99, 0.32, 0.62]
# ])
A = np.random.rand(400, 400)
# print(A)

n = A.shape[0]

g0 = np.array([1 if i == 0 else 0 for i in range(n)], dtype=np.float64)

def eigenind(v):
    projected = np.dot(A, v)

    return np.dot(projected, v) ** 2 / (np.dot(projected, projected) * np.dot(v, v))

def numerical_gradient(v):
    grad = np.array([0 for i in range(n)], dtype=np.float64)
    h = 0.01

    for k in range(n):
        right = eigenind(np.array([
            v[j] + (h if j == k else 0)
            for j in range(n)
        ], dtype=np.float64))
        left = eigenind(np.array([
            v[j] - (h if j == k else 0)
            for j in range(n)
        ], dtype=np.float64))

        grad[k] = (right - left) / (2 * h)

    return grad

def gradient(v):
    projected = np.dot(A, v)

    norm_v = np.dot(v, v)
    norm_projected = np.dot(projected, projected)

    dotted = np.dot(projected, v)

    grad = np.array([0 for i in range(n)], dtype=np.float64)

    for k in range(n):
        row_k = A[k, :]
        col_k = A[:, k]

        cross = np.dot(row_k + col_k, v)

        left = 2 * norm_v * norm_projected * dotted * cross
        right = 2 * dotted ** 2 * (
            v[k] * norm_projected +
            norm_v * np.dot(projected, A[:, k])
        )

        denom = (norm_v * norm_projected) ** 2

        grad[k] = (left - right) / denom

    return grad

# Wow that matches up really nicely yay
# print("Numerical gradient: ", numerical_gradient(g0))
# print("Gradient: ", gradient(g0))

g = g0
gamma = 0.1

scores = []

for i in range(100):
    ascent = g + gamma * gradient(g)
    g = ascent / np.linalg.norm(ascent)
    score = eigenind(g)

    scores.append(score)

    print(f"Step {i + 1} score: {score}")

print("Eigenvector:", g)
print("Eigenvalue:", np.linalg.norm(np.dot(A, g)) / np.linalg.norm(g))

# Using https://en.wikipedia.org/wiki/Rate_of_convergence#Order_estimation,
# we may determine an approximation for the convergence rate of the algorithm

# Tbh I'm not quite sure how accurate this is in terms of actually giving the
# convergence rate (I might have to look at the actual math later) but it gives
# that the convergence rate is roughly linear (although like some of the terms
# are far greater or lower)
"""
estimates = [
    log(abs((scores[k] - scores[k-1]) / (scores[k-1] - scores[k-2]))) / log(abs((scores[k-1] - scores[k-2]) / (scores[k-2] - scores[k-3])))
    for k in range(3, len(scores))
]

q = sum(estimates) / len(estimates)

print("Estimate convergence rates:", estimates)
print("Approximate convergence rate:", q)
"""
