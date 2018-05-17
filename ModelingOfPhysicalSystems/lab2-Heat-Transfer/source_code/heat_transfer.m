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

[K, Cw, Rho] = choose_material('Alumina');

T = bc_type1(Nt, Nx, Ny, dx, dy, dt, K, Cw, Rho);
