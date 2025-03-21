# multiAgents.py
# --------------
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


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent
from pacman import GameState

class ReflexAgent(Agent):
    """
    A reflex agent chooses an action at each choice point by examining
    its alternatives via a state evaluation function.

    The code below is provided as a guide.  You are welcome to change
    it in any way you see fit, so long as you don't touch our method
    headers.
    """


    def getAction(self, gameState: GameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {NORTH, SOUTH, WEST, EAST, STOP}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState: GameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"
        foodList = newFood.asList()
        score = successorGameState.getScore()
        if len(foodList):
            closestFoodDist = float('inf')
            for food in foodList:
                distance = manhattanDistance(newPos, food)
                if distance < closestFoodDist:
                    closestFoodDist = distance
            score += 1.0 / closestFoodDist
        ghostDistances = []
        for ghost in newGhostStates:
            ghostPos = ghost.getPosition()
            dist = manhattanDistance(newPos, ghostPos)
            ghostDistances.append(dist)
        if ghostDistances and min(ghostDistances) < 2:
            score -= 10

        return score

def scoreEvaluationFunction(currentGameState: GameState):
    """
    This default evaluation function just returns the score of the state.
    The score is the same one displayed in the Pacman GUI.

    This evaluation function is meant for use with adversarial search agents
    (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
    This class provides some common elements to all of your
    multi-agent searchers.  Any methods defined here will be available
    to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

    You *do not* need to make any changes here, but you can if you want to
    add functionality to all your adversarial search agents.  Please do not
    remove anything, however.

    Note: this is an abstract class: one that should not be instantiated.  It's
    only partially specified, and designed to be extended.  Agent (game.py)
    is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
    Your minimax agent (question 2)
    """

    def getAction(self, gameState):
        """
        Returns the minimax action from the current gameState using self.depth
        and self.evaluationFunction.
        """
        return self.minimax(gameState, 0, 0)[1] 

    def minimax(self, gameState, depth, agentIndex):
        if gameState.isWin() or gameState.isLose() or depth == self.depth:
            return self.evaluationFunction(gameState), None

        nextAgent = (agentIndex + 1) % gameState.getNumAgents()
        nextDepth = depth + 1 if nextAgent == 0 else depth
        actions = gameState.getLegalActions(agentIndex)

        if not actions:
            return self.evaluationFunction(gameState), None

        scores = []
        for action in actions:
            nextGameState = gameState.generateSuccessor(agentIndex, action)
            score = self.minimax(nextGameState, nextDepth, nextAgent)[0]
            scores.append((score, action))

        if agentIndex == 0:  
            return max(scores)
        else: 
            return min(scores)

class AlphaBetaAgent(MultiAgentSearchAgent):
    """
    Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState):
        """
        Returns the minimax action using self.depth and self.evaluationFunction
        """
        alpha = float('-inf')
        beta = float('inf')
        score, action = self.alphaBeta(gameState, 0, 0, alpha, beta)
        return action

    def alphaBeta(self, gameState, depth, agentIndex, alpha, beta):
        if gameState.isWin() or gameState.isLose() or depth == self.depth:
            return self.evaluationFunction(gameState), None

        numAgents = gameState.getNumAgents()
        nextAgent = (agentIndex + 1) % numAgents
        nextDepth = depth + 1 if nextAgent == 0 else depth
        actions = gameState.getLegalActions(agentIndex)

        if not len(actions):
            return self.evaluationFunction(gameState), None

        if agentIndex == 0:  
            value = float('-inf'), None
            for action in actions:
                successorValue = self.alphaBeta(gameState.generateSuccessor(agentIndex, action), nextDepth, nextAgent, alpha, beta)[0]
                if successorValue > value[0]:
                    value = successorValue, action
                if value[0] > beta:
                    return value
                alpha = max(value[0], alpha)
        else:
            value = float('inf'), None
            for action in actions:
                successorValue = self.alphaBeta(gameState.generateSuccessor(agentIndex, action), nextDepth, nextAgent, alpha, beta)[0]
                if successorValue < value[0]:
                    value = successorValue, action
                if value[0] < alpha:
                    return value
                beta = min(value[0], beta)
        return value

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
    Your expectimax agent (question 4)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the expectimax action using self.depth and self.evaluationFunction

        All ghosts should be modeled as choosing uniformly at random from their
        legal moves.
        """
        action = None
        v = -float('inf')
        for a in gameState.getLegalActions(0):
            if v < self.minValue(gameState.generateSuccessor(0, a), 1, 0):
                v = self.minValue(gameState.generateSuccessor(0, a), 1, 0)
                action = a
        return action

    def maxValue(self, gameState: GameState, depth):
        if depth == self.depth or len(gameState.getLegalActions(0)) == 0:
            return self.evaluationFunction(gameState)
        else:
            v = -float('inf')
            for a in gameState.getLegalActions(0):
                v = max(v, self.minValue(gameState.generateSuccessor(0, a), 1, depth))
            return v

    def minValue(self, gameState: GameState, agentIndex, depth):
        if len(gameState.getLegalActions(agentIndex)) == 0:
            return self.evaluationFunction(gameState)
        nrPossibleActions = len(gameState.getLegalActions(agentIndex))
        if agentIndex < (gameState.getNumAgents() - 1):
            v = 0
            for a in gameState.getLegalActions(agentIndex):
                v += self.minValue(gameState.generateSuccessor(agentIndex, a), agentIndex + 1, depth)
            return (v / float(nrPossibleActions))
        else:
            v = 0
            for a in gameState.getLegalActions(agentIndex):
                v += self.maxValue(gameState.generateSuccessor(agentIndex, a), depth + 1)
            return (v / float(nrPossibleActions))

        util.raiseNotDefined()

def betterEvaluationFunction(currentGameState:GameState):
    """
    Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
    evaluation function (question 5).

    DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"

    pacmanPos = currentGameState.getPacmanPosition()
    foodList = currentGameState.getFood().asList()
    foodScore = 0
    if (len(foodList) != 0):
        for f in foodList:
            foodScore += 1.0 / manhattanDistance(f, pacmanPos)

    capsuleList = currentGameState.getCapsules()
    capScore = 0
    if (len(capsuleList) != 0):
        for c in capsuleList:
            capScore += 1.0 / manhattanDistance(c, pacmanPos)

    score = foodScore + capScore
    score += 8 * currentGameState.getScore()
    score -= 6 * (len(foodList) + len(capsuleList))

    return score
    util.raiseNotDefined()

# Abbreviation
better = betterEvaluationFunction
