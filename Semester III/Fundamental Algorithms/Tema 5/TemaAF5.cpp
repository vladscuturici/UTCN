#include <iostream>
using namespace std;

int htable[10100];
int n = 9973, c1 = 1, c2 = 2, cnt=0, nr_tests=10;
int egMax, egMed, engMax, engMed, op;

int hashFunction(int k, int i)
{
    return (k%n + c1 * i + c2 * i * i) % n;
}

void insert(int k)
{
    if (cnt == n)
    {
        cout << "Tabela este plina, nu se poate insera "<<k<<"\n";
        return;
    }
    int i = 0;
    while (htable[hashFunction(k, i)] != 0)
        i++;
    htable[hashFunction(k, i)] = k;
    cnt++;
}

int search(int k)
{
    int i = 0;
    do
    {
        if (htable[hashFunction(k, i)] == k)
        {
            op = i;
            return hashFunction(k, i);
        }
        i++;
    }   
    while ((i != n) && (htable[hashFunction(k, i)] != 0));
    op = i;
    return -1;
}

void remove(int x)
{
    int p = search(x);
    if (p == -1)
    {
        cout << "Elementul nu se afla in tabela, nu se poate sterge";
        return;
    }
    htable[p] = -1;
    cnt --;
}
/*
void remove(int k)
{
    int i;
    if (search(k) == -1)
    {
        cout << "Elementul nu este in tabel, nu se poate sterge";
    }
    cnt--;
    i = op;
    while (htable[hashFunction(k, i+1)])
    {
        htable[hashFunction(k, i)] = htable[hashFunction(k, i + 1)];
        i++;
    }
    htable[hashFunction(k, i)] = 0;
}*/

void print()
{
    for (int i = 0; i < n; i++)
    {
        cout << "Pozitia "<<i<<" : "<<htable[i] << "\n";
    }
    cout << "\n";
}

int f_umplere(void)
{
    return (int)(((float)cnt / n) * 100);
}

void fill_table(int f)
{
    for (int i = 0; f_umplere() != f; i++)
    {
        insert(rand());
    }
    cout << 0 << "," << f_umplere() << '\n';
}

void demo()
{
    int x;
    insert(53);
    insert(412);
    insert(n + 12);
    insert(12);
    fill_table(95);
    insert(250);
    remove(250);
    cout <<"Factor de umplere " << 0 << "," << f_umplere() << '\n';
    cout << "search " << 53 << ", pozitia -> " << search(53) << '\n';
    cout << "search " << 412 << ", pozitia -> " << search(412) << '\n';
    cout << "search " << 12 << ", pozitia -> " << search(12) << '\n';
    cout << "search " << 250 << ", pozitia -> " << search(250) << '\n';
}

void perf()
{
    int x;
    egMax = engMax = egMed = engMed = 0;
    for (int t = 0; t < nr_tests; t++)
    {
        for (int i = 0; i < 1500; i++)
        {
            x = rand();
            while (search(x) < 0)
                x = rand();
            egMed += op;
            if (op > egMax)
                egMax = op;
        }
        for (int i = 0; i < 1500; i++)
        {
            x = rand();
                while (search(x) > 0)
                    x = rand();
            engMed += op;
            if (op > engMax)
                engMax = op;
        }
    }
    cout << "Eficienta gasite - mediu : " << (egMed / 1500) / nr_tests << ", maxim : " << egMax << '\n';
    cout << "Eficienta negasite - mediu : " << (engMed / 1500) / nr_tests << " , maxim : " << engMax << '\n';
    //cout << "Factor de umplere || Efort mediu gasite || Efort maxim gasite || Efort mediu negasite || Efort maxim negasite";
}

void perf_all()
{
    fill_table(80);
    perf();
    fill_table(85);
    perf();
    fill_table(90);
    perf();
    fill_table(95);
    perf();
    fill_table(99);
    perf();

}

void print(int n)
{
    for (int i = 0; i < n; i++)
        cout << htable[i] << " ";
    cout << '\n';
}

void demo_remove()
{
    int x, f = 80;
    fill_table(99);
    while (f_umplere() > 80)
    {
        x = (rand()) % n;
        while(search(htable[x]) == -1)
            x = (rand()) % n;
        remove(htable[x]);
    }
    cout << 0 << "," << f_umplere() << '\n';
    perf();
}

int main()
{
    srand(time(0));
    demo();
    //perf_all();
    //demo_remove();
}
//Scuturici Vlad Lucian - grupa 30225