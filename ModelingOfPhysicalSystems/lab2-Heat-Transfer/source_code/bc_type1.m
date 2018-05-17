function T = bc_type1(Nt, Nx, Ny, dx, dy, dt, K, Cw, Rho)
    T = zeros(Nx ,Ny, Nt);
    T(:, : ,1) = 20;
    T(1, :, :) = 10;
    T(Nx, :, :) = 10;
    T(:, 1, :) = 10;
    T(:, Ny, :) = 10;
    T((0.075 / dx):(Nx - 0.075 / dx), (0.075 / dy):(Ny - 0.075 / dy), :) = 80;
    
    T = equation(T, Nt, Nx, Ny, dx, dy, dt, K, Cw, Rho);
end