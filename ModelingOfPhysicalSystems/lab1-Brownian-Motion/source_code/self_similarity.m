function [] = self_similarity()

    c = [];
    a = rand(1000, 1);
    % we count correlation for random values
    for i = -49:50
        c = [c corr(a(100:900), a(100 + i:900 + i))];
    end
    
    subplot(2, 1, 1);
    plot(c);
    grid on;
    xlabel('x coordinate');
    ylabel('autocorrelation');
    
    x = 0;
    
    for i = 2:1000
        x = [x x(i - 1) + randn()];
    end
    
    c = [];
    % we count correlation for Brownian
    for i = -49:50
        c = [c corr(x(100:900)', x(100 + i:900 + i)')];
    end
    
    subplot(2, 1, 2);
    stairs(-49:50, c);
    
    plot(c);
    grid on;
    xlabel('x coordinate');
    ylabel('autocorrelation');
end