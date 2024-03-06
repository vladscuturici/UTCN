#include <iostream>
#include <vector>

using namespace std;

int WHITE = 23012, GRAY = -31209, BLACK = 210931;

struct Graph
{
    vector<int>* ad;
    int V, E, timp, * td, * tf, * color, * p;
};

void DFS_Visit(Graph G, int i)
{
    G.timp++;
    G.td[i] = G.timp;
    G.color[i] = GRAY;
    for (int j = 0; j < G.ad[i].size(); j++) 
    {
        int v = G.ad[i][j];
        if (G.color[v] == WHITE) {
            G.p[v] = i;
            DFS_Visit(G, v);
        }
    }
    G.tf[i] = ++G.timp; 
    G.color[i] = BLACK;
}

void DFS(Graph G)
{
    for (int i = 0; i < G.V; i++)
    {
        G.p[i] = -1;
        G.color[i] = WHITE;
    }
    G.timp = 0;
    for (int i = 0; i < G.V; i++)
    {
        if (G.color[i] == WHITE)
            DFS_Visit(G, i);
    }
}

void demo()
{
    Graph G;
    G.V = 7;
    G.E = 7;
    G.ad = new vector<int>[G.V];
    G.td = new int[G.V];
    G.tf = new int[G.V];
    G.color = new int[G.V];
    G.p = new int[G.V];
    G.timp = 0;
    G.ad[0].push_back(1);
    G.ad[0].push_back(2);
    G.ad[0].push_back(3);
    G.ad[1].push_back(4);
    G.ad[2].push_back(4);
    G.ad[3].push_back(4);
    G.ad[4].push_back(5);
    G.ad[4].push_back(6);
    G.ad[5].push_back(6);
    for (int i = 0; i < G.V; i++)
    {
        cout << i << " conectat la ";
        for (int j = 0; j < G.ad[i].size(); j++)
        {
            cout << G.ad[i][j] << " ";
        }
        cout << '\n';
    }
    DFS(G);
    for (int i = 0; i < G.V; i++)
    {
        cout << i << "-> timp finalizare : " << G.tf[i] << ": parinte : " << G.p[i] << '\n';
    }
}

int main()
{
    demo();
    return 0;
}