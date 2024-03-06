#include <iostream>
#include <cmath>
using namespace std;

struct Node {
	int key;
	int size;
	Node* left;
	Node* right;
	Node* parent;
	int level;
};

int k = 1;

Node* addNode(int key)
{
	Node* x = new Node();
	x->key = key;
	x->size = 1;
	x->left = NULL;
	x->right = NULL;
	return x;
}

void addSize(Node* Tree)
{
	if (Tree->right)
		addSize(Tree->right);
	if (Tree->left)
		addSize(Tree->left);
	if (Tree->right)
		Tree->size += Tree->right->size;
	if (Tree->left)
		Tree->size += Tree->left->size;
}

void prettyPrint(Node* x, int level)
{
	if (x != NULL)
	{
		prettyPrint(x->right, level + 1);
		for (int i = 1; i <= level; i++)
			printf("     ");
		cout << x->key;
		cout << "\n";
		prettyPrint(x->left, level + 1);
	}
}

void checkBalance(Node* Tree)
{
	if (Tree->right)
		checkBalance(Tree->right);
	if (Tree->left)
		checkBalance(Tree->left);
	if (Tree->right == NULL && Tree->left == NULL)
		return;
	if (Tree->right == NULL)
	{
		if (Tree->left->size > 1)
			k = 0;
		return;
	}
	if (Tree->left == NULL)
	{
		if (Tree->right->size > 1)
			k = 0;
		return;
	}
	if (abs(Tree->left->size - Tree->right->size) > 1)
		k = 0;
}

Node* buildTree(Node* Tree, int i, int j)
{
	if (i <= j)
	{
		int m = (i + j) / 2;
		Node* x = addNode(m);
		x->level = 1;
		x->size = 1;
		x->parent = Tree;
		x->left = buildTree(x, i, m - 1);
		x->right = buildTree(x, m + 1, j);
		if (x->left != NULL)
			x->level = x->left->level + x->level;
		if (x->right != NULL)
			x->level = x->level + x->right->level;
		return x;
	}
	else 
		return NULL;
}

int main()
{
	int a;
	Node* Tree = addNode(5);
	Tree = buildTree(NULL, 1, 11);
	addSize(Tree);
	prettyPrint(Tree, 0);
	checkBalance(Tree);
	cout << k;
	return 0;
}