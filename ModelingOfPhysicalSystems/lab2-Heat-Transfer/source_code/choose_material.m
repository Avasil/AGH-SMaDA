% can be Alumina, Cooper or Stainless Steel
function [K, Cw, Rho] = choose_material(material)
    if strcmp(material, 'Alumina')
        K=237;
        Cw=900;
        Rho=2700;
    elseif strcmp(material, 'Cooper')
        K=401;
        Cw=380;
        Rho=8920;
    else
        K=58;
        Cw=450;
        Rho=7860;
    end;  
end