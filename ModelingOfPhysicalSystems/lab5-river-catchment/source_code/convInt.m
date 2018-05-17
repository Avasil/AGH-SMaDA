function result = convInt(c_in, t, tt)
    % c_in - input values
    % t    - timestamp
    % tt   - mean residence time
    lambda = 4.696e-3;
    result = 0;
    for i = 1:t
        result = result + c_in(i, 2) * ... 
            1/tt * exp(-(t - i)/tt) * ...
            exp(-lambda *(t - i));
    end;
end