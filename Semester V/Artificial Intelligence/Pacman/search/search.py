# search.py
# ---------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).

#Proiect pe echipa cu Darolti Laura

"""
In search.py, you will implement generic search algorithms which are called by
Pacman agents (in searchAgents.py).
"""

import util

class SearchProblem:
    """
    This class outlines the structure of a search problem, but doesn't implement
    any of the methods (in object-oriented terminology: an abstract class).

    You do not need to change anything in this class, ever.
    """

    def getStartState(self):
        """
        Returns the start state for the search problem.
        """
        util.raiseNotDefined()

    def isGoalState(self, state):
        """
          state: Search state

        Returns True if and only if the state is a valid goal state.
        """
        util.raiseNotDefined()

    def getSuccessors(self, state):
        """
          state: Search state

        For a given state, this should return a list of triples, (successor,
        action, stepCost), where 'successor' is a successor to the current
        state, 'action' is the action required to get there, and 'stepCost' is
        the incremental cost of expanding to that successor.
        """
        util.raiseNotDefined()

    def getCostOfActions(self, actions):
        """
         actions: A list of actions to take

        This method returns the total cost of a particular sequence of actions.
        The sequence must be composed of legal moves.
        """
        util.raiseNotDefined()


def tinyMazeSearch(problem):
    """
    Returns a sequence of moves that solves tinyMaze.  For any other maze, the
    sequence of moves will be incorrect, so only use this for tinyMaze.
    """
    from game import Directions
    s = Directions.SOUTH
    w = Directions.WEST
    return  [s, s, w, s, w, w, s, w]

def depthFirstSearch(problem: SearchProblem):
    """
    Search the deepest nodes in the search tree first.

    Your search algorithm needs to return a list of actions that reaches the
    goal. Make sure to implement a graph search algorithm.

    To get started, you might want to try some of these simple commands to
    understand the search problem that is being passed in:
    """
    # print("Start:", problem.getStartState())
    # print("Is the start a goal?", problem.isGoalState(problem.getStartState()))
    # print("Start's successors:", problem.getSuccessors(problem.getStartState()))
    "*** YOUR CODE HERE ***"
    
    visited = []
    path = []
    nextPath = []
    stack = util.Stack()
    stack.push((problem.getStartState(), path))
    while(not stack.isEmpty()):
        node, path = stack.pop()
        if problem.isGoalState(node):
            return path 
        if node not in visited:
            visited.append(node)
            for successor, action, stepCost in problem.getSuccessors(node):
                if successor not in visited:
                    nextPath = path + [action]
                    stack.push((successor, nextPath))

def breadthFirstSearch(problem: SearchProblem):
    """Search the shallowest nodes in the search tree first."""
    "* YOUR CODE HERE *"
    queue = util.Queue()
    visited = []
    start_node = problem.getStartState()

    if problem.isGoalState(start_node):
        return []


    # print("the start node is:", start_node)

    queue.push((start_node, []))
    # visited.append(start_node)

    while not queue.isEmpty():

        n, actions = queue.pop()
        # print("queue popped")
        # newqueue = queue
        # while not newqueue.isEmpty():
        #     n1, actions1 = newqueue.pop()
        #     print(n1)

        # print("n is", n)
        # print("Visited nodes are:", visited)
        if n not in visited:
            visited.append(n)

            if problem.isGoalState(n):
                return actions

            for successor, action, cost in problem.getSuccessors(n):
                queue.push((successor, actions + [action]))

    # return []
    #util.raiseNotDefined()

def uniformCostSearch(problem: SearchProblem):
    """Search the node of least total cost first."""
    "* YOUR CODE HERE *"
    queue = util.PriorityQueue()
    visited = []
    start_node = problem.getStartState()

    if problem.isGoalState(start_node):
        return []

    print("the start node is:", start_node)

    queue.push((start_node, [], 0), 0)
    # visited.append(start_node)

    while not queue.isEmpty():

        n, actions,final_cost = queue.pop()
        # print("queue popped")
        # newqueue = queue
        # while not newqueue.isEmpty():
        #     n1, actions1 = newqueue.pop()
        #     print(n1)

        # print("n is", n)
        # print("Visited nodes are:", visited)
        if n not in visited:
            visited.append(n)

            if problem.isGoalState(n):
                return actions

            for successor, action, cost in problem.getSuccessors(n):
                new_actions = actions + [action]
                if cost is not None:
                    new_cost = final_cost + cost
                    # Push the successor, new actions, and new cost to the priority queue
                    queue.push((successor, new_actions, new_cost), new_cost)
                else:
                    # If there's no cost, use 0 as the default
                    queue.push((successor, new_actions, final_cost), final_cost)

def nullHeuristic(state, problem=None):
    """
    A heuristic function estimates the cost from the current state to the nearest
    goal in the provided SearchProblem.  This heuristic is trivial.
    """
    return 0

def aStarSearch(problem, heuristic=nullHeuristic):
    queue = util.PriorityQueue()
    queue.push((problem.getStartState(), [], 0), 0)
    visited = []
    # open_list = []
    # closed_list = set()
    # open_list.append((start, [], 0))

    while not queue.isEmpty():
        state, actions, cost = queue.pop()
        if state not in visited:
            visited.append(state)
            if problem.isGoalState(state):
                return actions
            for successor, action, new_cost in problem.getSuccessors(state):
                # newPath = currentPath + [action]
                # newCost = currentCost + stepCost
                # totalCost = newCost + heuristic(successor, problem)
                # fringe.push((successor, newPath, newCost), totalCost)
                queue.push((successor, actions + [action], cost + new_cost), heuristic(successor, problem) + cost + new_cost)
    # return []
    # util.raiseNotDefined()


# Abbreviations
bfs = breadthFirstSearch
dfs = depthFirstSearch
astar = aStarSearch
ucs = uniformCostSearch
