% preparing workspace
close all; clear all; clc;

% setting up parameters

% metal plate
A = 0.2; 
% heater
B = 0.05; 
% distance between computational nodes in x direction
dx = 0.01;
% distance between computational nodes in y direction
dy = 0.01;
% time step
dt = 0.1;
% number of time steps
Nt = 1000;
% number of computational nodes in x direction
Nx = A/dx;
% number of computational nodes in y direction
Ny = A/dy;

h = 0.002;
P = 100;
% temp outside
Tout = 10;

[K, Cw, Rho] = choose_material('Alumina');

T = zeros(Nx, Ny, Nt);
T(:, :, 1) = 20;

for t = 1:Nt - 1
    for i = 2:Nx - 1
        for j = 2:Ny - 1
            Tdiff = -5.678 * 10^-8 * T(i, j, t) * T(i, j, t) * T(i, j, t) * T(i, j, t) * (T(i, j, t) - Tout);
            if (~((i >= (0.075 / dx) &&  i <= (Nx - 0.075 / dx)) && (j >= (0.075 / dy) &&  j <= (Nx - 0.075 / dy))) || t >= (1/dt))
                T(i, j, t + 1) = T(i, j, t) ...
                    + (K * dt / (Cw * Rho * dx.^2)) * (T(i + 1, j, t) - 2*T(i, j, t) + T(i - 1, j, t)) ...
                    + (K * dt / (Cw * Rho * dy.^2)) * (T(i, j + 1, t) - 2*T(i, j, t) + T(i, j - 1, t)) + Tdiff;
            else
                if t < ( 1 / dt)
                    T(i, j, t + 1) = T(i, j, t) ... 
                    	+ (K * dt / (Cw * Rho * dx.^2)) * (T(i + 1, j, t) - 2*T(i, j, t) + T(i - 1, j, t)) ...
                     	+ (K * dt / (Cw * Rho * dy.^2)) * (T(i, j + 1, t) - 2*T(i, j, t) + T(i, j - 1, t)) ...
                    	+ Tdiff + (P * dt) / (Cw * B.^2 * h * Rho);
               end;
            end;
        end;
    end;
  T(1, 1, t + 1) = T(2, 2, t + 1);
  T(Nx, Ny, t + 1) = T(Nx - 1, Ny - 1, t + 1);
  T(1, Ny, t + 1) = T(2, Ny - 1, t + 1);
  T(Nx, 1, t + 1) = T(Nx - 1, 2, t + 1);
  T(1, :, t + 1) = T(2, :, t + 1);
  T(Nx, :, t + 1) = T(Nx - 1, :, t + 1);
  T(:, 1, t + 1) = T(:, 2 , t + 1);
  T(:, Ny, t + 1) = T(:, Ny - 1, t + 1);
end;

[XX, YY] = meshgrid(dx:dx:A, dy:dy:A);

for tt=1:t+1
  surf(XX, YY, T(:, :, tt));
  title(['Simulation time = ' num2str(tt * dt) ' (s)']);
  xlabel('x (m)');
  ylabel('y (m)');
  zlabel('Temperature (degC)');
  axis([0 A 0 A Tout 30]);        
  drawnow;
  pause(0.1);
end;