% start from physical model and go to mathematical one
% A = 20 x 20 cm 
% B = 5 x 5 cm
% Fourier law describes sth about heat, temp in Kelwins (doesnt matter tho)
% 
% plate aluminium, stainless steel, copper
% if gradient == 0 it means there is no heat flux going to or from plate
%
% room temperature at the beginning


clear; clc; close all;

timesteps = 100;

A = 25;
B = 5;
T = zeros(A, A, 1);
T(:, :, 1) = 10;
rangeX = (A - B) / 2 + 1; 
rangeY = (A + B) / 2 - 1;
T(rangeX:rangeY, rangeX:rangeY, 1) = 80;

dt = 0.1;

rho = 2700;
cw = 900;
K = 237;

dx = 0.01;
dy = 0.01;

T = equation(T, rho, cw, K, dx, dy, dt, timesteps, A, B);
% measure it, prepare X and Y matrices, defining left and right borders +
% resolution
% surf(T(:, :, number_of_timesteps));

[XX, YY] = meshgrid(1:A,1:A);
surf(XX, YY, T(:, :, timesteps));

title(['Simulation time = ' num2str(timesteps*dt) ' (s)']);
xlabel('x (m)');
ylabel('y (m)');
zlabel('Temperature (degC)');

% how much time for each material to go to steady state (what is that
% criteria?)

%  round numbers
% A = 0.2, B = 0.05, dx = 0.01 dy = 0.01
% if BC == 1 leci T(1, :, :) = T2
% if I change resolution I should get the same time in seconds
% check stability of algorithm, find border / relationship between kX and
% kY

% criteria of steady state -> squared mean difference of all places *
% boundary condition

