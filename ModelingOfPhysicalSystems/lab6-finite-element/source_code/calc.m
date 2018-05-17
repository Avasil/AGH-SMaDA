F = -1000; % kiadubg force
L = 1.5; % beam length
E = 1.8 * 10^8; % Young's modulus for stainless steel
g = 1; % beam thickness
d = 0.2; % beam width

J = (g * d^3) / 12;
h = (F * L^3) / (3 * E * J)