function [] = mean_square(dims, timesteps, nparts)
    if dims == 1
        x =  zeros(nparts, 1);
        for i=2:timesteps
            x = [x x(:, i - 1) + randn(nparts, 1)];
        end;
        
        plot((sum(x.^2)/nparts)');
    elseif dims == 2
        x = zeros(nparts, 1);
        y = zeros(nparts, 1);
      
        for i=2:timesteps
            x = [x x(:, i - 1) + randn(nparts, 1)];
            y = [y y(:, i - 1) + randn(nparts, 1)];
        end;
        
        plot((sum(x.^2) + sum(y.^2))/nparts');
    else
        x = zeros(nparts, 1);
        y = zeros(nparts, 1);
        z = zeros(nparts, 1);
        
        for i=2:timesteps
            x = [x x(:, i - 1) + randn(nparts, 1)];
            y = [y y(:, i - 1) + randn(nparts, 1)];
            z = [z z(:, i - 1) + randn(nparts, 1)];
        end;
        
        plot((sum(x.^2) + sum(y.^2) + sum(z.^2))/nparts');
    end
    xlabel('timestep');
    ylabel('Mean squared displacement');
end