input = importdata('opady.prn');
output = importdata('dunaj.prn');

in_count = size(input, 1);
% we will have to compare subsequent rmse
% so we may initialize one before loop
rmsePrev = 100000000;
% lower the step to get more precise tt value
step = 0.001;

for tt = 1:step:1000
    integralResult = zeros(in_count, 1);
    % we don't have output data for 0 .. 161
    for i = 162:in_count
        integralResult(i) = convInt(input, i, tt);
    end
    errors = (output(:, 2) - integralResult).^2;
    
    rmseCurr = sqrt(sum(errors) / (in_count - 161));
    % when this condition is met search is over - current value of tt is
    % the result
    if (rmseCurr > rmsePrev)
        break;
    else
        rmsePrev = rmseCurr;
    end
end

disp(tt);