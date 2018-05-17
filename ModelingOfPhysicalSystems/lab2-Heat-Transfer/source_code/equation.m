function T = equation(T, Nt, Nx, Ny, dx, dy, dt, K, Cw, Rho)
    for t = 1:Nt - 1
        for i = 2:Nx - 1
            for j = 2:Ny - 1
                if ~((i >= (0.075 / dx) &&  i <= (Nx - 0.075 / dx)) && (j >= (0.075 / dy) &&  j <= (Nx - 0.075 / dy)))
                    T(i, j, t + 1) = T(i, j, t) ...
                        + (K * dt / (Cw * Rho * dx.^2)) * (T(i + 1, j, t) - 2*T(i, j, t) + T(i - 1, j, t)) ...
                        + (K * dt / (Cw * Rho * dy.^2)) * (T(i, j + 1, t) - 2*T(i, j, t) + T(i, j - 1, t));
                end;
            end;
        end;
        
        [XX, YY] = meshgrid(dx:dx:0.2, dy:dy:0.2);
        surf(XX, YY, T(:, :, t));
        title(['Simulation time = ' num2str(t * dt) ' (s)']);
        xlabel('x (m)');
        ylabel('y (m)');
        zlabel('Temperature (degC)');
        
        drawnow;
        pause(0.1);
    end;
end