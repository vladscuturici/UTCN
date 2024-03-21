package org.example.model;

public class Calculator {
    public Calculator(){
    }
    public Polinom adunare(Polinom p1, Polinom p2){
        Polinom r = new Polinom();
        int n = p1.getGrad();
        if (n<p2.getGrad())
            n=p2.getGrad();
        for(int i=0;i<=n;i++){
            if((p1.getMonom(i)+p2.getMonom(i)) != 0)
                r.adaugaMonom(p1.getMonom(i)+p2.getMonom(i), i);
        }
        return r;
    }

    public Polinom scadere(Polinom p1, Polinom p2){
        Polinom r = new Polinom();
        int n = p1.getGrad();
        if (n<p2.getGrad())
            n=p2.getGrad();
        for(int i=0;i<=n;i++){
            if((p1.getMonom(i)-p2.getMonom(i)) != 0)
                r.adaugaMonom(p1.getMonom(i)-p2.getMonom(i), i);
        }
        return r;
    }

    public Polinom inmultire(Polinom p1, Polinom p2){
        Polinom r = new Polinom();
        int n1 = p1.getGrad(), n2 = p2.getGrad(), grad;
        double m, t;
        for(int i=0;i<=n1;i++){
            for(int j=0;j<=n2;j++){
                m = p1.getMonom(i) * p2.getMonom(j);
                grad = i+j;
                if(r.monom.isEmpty())
                    r.adaugaMonom(m, grad);
                else {
                    t = r.getMonom(grad) + m;
                    r.adaugaMonom(t, grad);
                }
            }
        }
        return r;
    }

    public Polinom impartire(Polinom p1, Polinom p2)
    {
        Polinom cat = new Polinom();
        Polinom rest = new Polinom();
        rest = p1;
        while (p2.getGrad() <= rest.getGrad())
        {
            double c = rest.getMonom(rest.getGrad()) / p2.getMonom(p2.getGrad());
            int g = rest.getGrad() - p2.getGrad();
            Polinom term = new Polinom();
            term.adaugaMonom(c, g);
            cat = adunare(cat, term);
            rest = scadere(rest, inmultire(term, p2));
        }
        return cat;
    }


    public Polinom derivare(Polinom p){
        Polinom r = new Polinom();
        int n = p.getGrad();
        for(int i=0;i<=n;i++){
            r.adaugaMonom(p.getMonom(i) * i, i-1);
        }
        r.C = false;
        return r;
    }

    public Polinom integrare(Polinom p){
        Polinom r = new Polinom();
        int n = p.getGrad();
        for(int i=0;i<=n;i++){
            r.adaugaMonom(p.getMonom(i) / (i+1), i+1);
        }
        r.C = true;
        return r;
    }
}

