clear; clc;

figure;
subplot(1, 2, 1);
brownian(1, 1000);
grid on;

subplot(1, 2, 2);
brownian(3, 1000);
grid on;

figure;
self_similarity();

figure;
subplot(3, 2, 1);
mean_square(1, 50, 50);
grid on;
title('1 dimension');
subplot(3, 2, 2);
mean_square(1, 1000, 1000);
grid on;
title('1 dimension');

subplot(3, 2, 3);
mean_square(2, 50, 50);
grid on;
title('2 dimensions');
subplot(3, 2, 4);
mean_square(2, 1000, 1000);
grid on;
title('2 dimensions');

subplot(3, 2, 5);
mean_square(3, 50, 50);
grid on;
title('3 dimensions');

subplot(3, 2, 6);
mean_square(3, 1000, 1000);
grid on;
title('3 dimensions');

figure;
subplot(1, 2, 1);
particles_density(1, 100, 1000);
grid on;
subplot(1, 2, 2);
particles_density(2, 100, 1000);
grid on;

