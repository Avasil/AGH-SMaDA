function result = balance_equation(x)
    global S as
    ta = 0.53;       
    ta_p = 0.06;
    aa = 0.30;
    aa_p = 0.31;        
    c  = 2.7;               
    sigma = 5.67 * (10^-8);

    result = [ -1 * ta * (1 - as) * (S/4) + c * (x(1) - x(2)) + ...
            sigma * (x(1) ^ 4) * (1 - aa_p) - sigma * (x(2) ^ 4);
            -1 * (1 - aa - ta + as*ta) * S / 4 - c * (x(1) - x(2)) - ...
            sigma * (x(1) ^ 4) * (1 - ta_p - aa_p) + 2 * sigma * x(2)^4;
        ];
end