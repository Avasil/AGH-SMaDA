function [] = particles_density(dims, timesteps, nparts)
    if dims == 1
        x =  zeros(nparts, 1);
        for i=2:timesteps
            x = [x x(:, i - 1) + randn(nparts, 1)];
        end;
        histogram(x);
        xlabel('x coordinate');
        ylabel('number of particles');
        
    elseif dims == 2
        x = zeros(nparts, 1);
        y = zeros(nparts, 1);
      
        for i=2:timesteps
            x = [x x(:, i - 1) + randn(nparts, 1)];
            y = [y y(:, i - 1) + randn(nparts, 1)];
        end;
        histogram2(x, y);
        xlabel('x coordinate');
        ylabel('y coordinate');
        zlabel('number of particles');
end