function err = objective(tt)
    input = importdata('opady.prn');
    output = importdata('dunaj.prn');
    result = zeros(1, 161);
    % we don't have output data for 0 .. 161
    for i=162:size(input, 1)
        result(i, 1) = convInt(input, i, tt);
    end
    
    err = sum((output(:,2 )- result(:, 1)).^2);
end