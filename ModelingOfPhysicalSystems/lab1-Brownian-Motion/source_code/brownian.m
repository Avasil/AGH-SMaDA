function [] = brownian(trajectories, nparts)

    for i = 1:trajectories
        x = 0;
        y = 0;
        for j = 2:nparts
            x = [x x(j - 1) + randn()];
            y = [y y(j - 1) + randn()];
        end
        
        plot(x, y);
        hold on;
    end
    
    xlabel('x coordinate');
    ylabel('y coordinate');
    hold off;
end