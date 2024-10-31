import matplotlib
import numpy as np

matplotlib.use("tkagg")

import matplotlib.pyplot as plt
from math import sqrt

fig = plt.figure()
ax = fig.add_subplot(projection="3d")

POINTS = 20
dl = 2 / (POINTS - 1)

points = set()

# Change this later
# A = np.array([
#     [0.32, 0.53, 0.69],
#     [0.12, 0.33, 0.54],
#     [0.99, 0.32, 0.62]
# ])
# Eigenvector is
# [0.569246, 0.377254, 0.730506]

# eigenvector = np.array([0.569246, 0.377254, 0.730506])

# Because 3 is odd there's always going to be a real eigenvalue so yeah we
# can't exactly test what that would look like in this case
A = np.array([
    [0, -1,  0],
    [1,  0,  0],
    [0,  0,  1]
])

eigenvector = np.array([0, 0, 1])

def normalize(v):
    l = sqrt(v[0] ** 2 + v[1] ** 2 + v[2] ** 2)
    return (v[0] / l, v[1] / l, v[2] / l)

def eigenind(v):
    projected = np.dot(A, v)

    return np.dot(projected, v) ** 2 / (np.dot(projected, projected) * np.dot(v, v))

# Generate cube
for x in [0, POINTS - 1]:
    for y in range(0, POINTS):
        for z in range(0, POINTS):
            points.add(
                (x * dl - 1, y * dl - 1, z * dl - 1)
            )

for y in [0, POINTS - 1]:
    for x in range(0, POINTS):
        for z in range(0, POINTS):
            points.add(
                (x * dl - 1, y * dl - 1, z * dl - 1)
            )

for z in [0, POINTS - 1]:
    for y in range(0, POINTS):
        for x in range(0, POINTS):
            points.add(
                (x * dl - 1, y * dl - 1, z * dl - 1)
            )

points = [normalize(p) for p in points]

# print("Eigenindicator check (should be 1):", eigenind(eigenvector))

x_points = []
y_points = []
z_points = []

x_points_marker = []
y_points_marker = []
z_points_marker = []

for p in points:
    # Looks somewhat surprisingly okay ngl
    v = np.array([p[0], p[1], p[2]])
    w = np.array([p[0], p[1], p[2]])

    v = np.dot(A, v)

    x_points.append(v[0])
    y_points.append(v[1])
    z_points.append(v[2])

    mult = eigenind(w)
    w *= mult
    # print(mult)

    x_points_marker.append(w[0])
    y_points_marker.append(w[1])
    z_points_marker.append(w[2])


# ax.scatter(x_points, y_points, z_points, marker="o", c="blue")
ax.scatter(x_points_marker, y_points_marker, z_points_marker, marker="o", c="red")

ax.set_xlabel("$x$ axis")
ax.set_ylabel("$y$ axis")
ax.set_zlabel("$z$ axis")
ax.set_box_aspect([1, 1, 1])

plt.show()
