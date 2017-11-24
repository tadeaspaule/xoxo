from random import randint

# XOXO - a learning program that learns to be unbeatable in Tic Tac Toe, by Tadeáš Paule
# Basics concepts you will see used in the code below:
#   -> the state of the board is represented by a 9-digit where 1's are empty tiles, 2's are opponent symbols,
#       and 3's are XOXO's symbols. The numbers go from left to right, from top to bottom, so for example
#       111111111 is an empty board, 111131111 is a board with XOXO's symbol in the middle tile
#   ->


# Basic terms you will see used in the code below:
#   -> gamestate / state = refers to the current state of the board
#   -> reward = refers to the reward of a particular gamestate, 1 is a victory, 0.5 is a draw and -1 is a loss


def get_state_reward(state):
    if is_win(state):
        return 1.0
    if is_win(flip_state(state)):  # if the opposite view is a victory it means the opponent won
        return -1.0
    if str(state).count("1") == 0:  # no empty tiles (1's) left, meaning it's a draw
        return 0.5
    return -0.04  # -0.04 is the living penalty, meant to encourage faster victory


def is_win(state):
    state = str(state)
    # below all the various means of winning are checked
    if state[0] == state[1] == state[2] == "3":
        return True
    if state[3] == state[4] == state[5] == "3":
        return True
    if state[6] == state[7] == state[8] == "3":
        return True
    if state[0] == state[3] == state[6] == "3":
        return True
    if state[1] == state[4] == state[7] == "3":
        return True
    if state[2] == state[5] == state[8] == "3":
        return True
    if state[0] == state[4] == state[8] == "3":
        return True
    if state[2] == state[4] == state[6] == "3":
        return True
    # if no winning condition is met, returns false
    return False


def flip_state(state):
    # flips the perspective, making enemy symbols your symbols and vice versa

    return int(str(state).replace("2", "m").replace("3", "2").replace("m", "3"))


def generate_moves(valid_differences):
    # generates an initial dictionary of move-value pairs

    # valid_differences is an array of integers that are the allowed differences of count("3") and count("2")
    # in practice it means this: your moves can either have a difference of 1 or 0, 1 is when there's
    #  more of your symbols than the opponent's by 1 and 0 means there's a same amount of X's and O's.
    #  For your opponent it's the reverse, the difference can be either -1 or 0.
    #  All moves has differences of -1, 0, and 1.
    s = {}
    for i1 in range(1, 4):
        for i2 in range(1, 4):
            for i3 in range(1, 4):
                for i4 in range(1, 4):
                    for i5 in range(1, 4):
                        for i6 in range(1, 4):
                            for i7 in range(1, 4):
                                for i8 in range(1, 4):
                                    for i9 in range(1, 4):
                                        x = int(str(i1)+str(i2)+str(i3)+str(i4)+str(i5)+str(i6)+str(i7)+str(i8)+str(i9))
                                        if str(x).count("3") - str(x).count("2") in valid_differences and not (is_win(x) and is_win(flip_state(x))):
                                            s[x] = get_state_reward(x)
    return s


def get_possible_opponent_moves(state):
    # returns a dictionary of all possible opponent moves and their values

    empty_spaces = [c for c in range(len(str(state))) if str(state)[c] == "1"]  # indexes of empty tiles
    options = {}
    for o in [int(str(state)[:i] + "2" + str(state)[i + 1:]) for i in empty_spaces]:
        options[o] = all_opponent_moves[o]

    return options


def get_my_possible_moves(state):
    # returns a dictionary of all XOXO's possible moves and their values

    empty_spaces = [c for c in range(len(str(state))) if str(state)[c] == "1"]  # indexes of empty tiles
    options = {}
    for o in [int(str(state)[:i] + "3" + str(state)[i + 1:]) for i in empty_spaces]:
        options[o] = your_moves[o]

    return options


def get_state_value(state):
    # used in the first layer to assign tentative values

    if all_moves[state] in [-1, 0.5, 1]:  # if it's a win, loss, or draw, return that value
        return all_moves[state]
    possible_opp_moves_all = get_possible_opponent_moves(state)
    possible_opp_moves_keys = list(possible_opp_moves_all.keys())
    second_branch_my_possible_moves = []  # will store all possible moves for the next turn (after the opponent's played)

    result = min(list(possible_opp_moves_all.values()))  # gets the worst case scenario value of all possible opponent moves
    if result not in [-1, 0.5]:  # if it's not a loss or a draw
        for opp_move in possible_opp_moves_keys:
            second_branch_my_possible_moves.extend(list(get_my_possible_moves(opp_move).keys()))
        move_values = [your_moves[move] for move in list(your_moves.keys()) if move in second_branch_my_possible_moves]
        # if there's even one possibility of winning on the next turn, this returns a good value. That's why this is only tentative,
        #  some moves lead to victory more reliably than others. Distinguishing that is done in the later layers,
        #  this results in a rough estimation of how far from victory a move theoretically is.
        return result + max(move_values)*0.8
    else:
        return 0.8 * result


def pick_move(state):
    possible_moves = get_my_possible_moves(state)
    best_choices = [move for move in list(possible_moves.keys()) if possible_moves[move] == max(list(possible_moves.values()))]
    return best_choices[0]  # out of all the choices with the highest value, plays the first one.


def first_layer():
    # uses the get_state_value method to assign tentative values to moves

    while True:
        unchanged = 0
        for move in list(your_moves.keys()):
            if your_moves[move] == get_state_value(move):
                unchanged += 1
            else:
                your_moves[move] = get_state_value(move)
        # the two prints below show progress in changing values
        print("finished a round")
        print("unchanged this round: " + str(unchanged))
        if unchanged == len(your_moves.keys()):  # if it goes through all moves without changing anything, it's time to quit :)
            break


def second_layer(numofgames, accuracy):
    # in this part, XOXO plays against itself

    used_random = 0  # not a crucial variable at all, used only for tracking how many times XOXO played a random move

    print("now playing against itself")
    for game in range(numofgames):
        gamestate = 111111111
        reward = 0
        gameflow_first = []
        gameflow_second = []
        while True:
            if randint(1, 101) <= accuracy:
                gamestate = pick_move(gamestate)
            else:
                used_random += 1
                poss_moves = list(get_my_possible_moves(gamestate).keys())
                gamestate = poss_moves[randint(0, len(poss_moves) - 1)]
            gameflow_first.append(gamestate)
            if get_state_reward(gamestate) in [-1, 0.5, 1]:
                gameflow_second.append(flip_state(gamestate))
                reward += get_state_reward(gamestate)
                break

            gamestate = flip_state(gamestate)

            if randint(1, 101) <= accuracy:
                gamestate = pick_move(gamestate)
            else:
                used_random += 1
                poss_moves = list(get_my_possible_moves(gamestate).keys())
                gamestate = poss_moves[randint(0, len(poss_moves) - 1)]
            gameflow_second.append(gamestate)

            gamestate = flip_state(gamestate)

            if get_state_reward(gamestate) in [-1, 0.5, 1]:  # game ends
                gameflow_first.append(gamestate)
                reward += get_state_reward(gamestate)
                break

        for move in list(your_moves.keys()):
            # value of moves made is increased (or decreased if XOXO lost).
            if move in gameflow_first:  # if the first played that move.
                your_moves[move] += 0.1*reward
            if move in gameflow_second:  # if the second played that move.
                if reward != 0.5:  # if it wasn't a draw, the reward increasing/lowering is reversed (if the reward was -1 for the second player,
                                    #  that means the first player won, and vice versa
                    your_moves[move] -= 0.1*reward
                else:
                    your_moves[move] += 0.1*reward

    print("used random moves " + str(used_random) + " times")


def third_layer(rounds_against_random):
    # in this layer, XOXO plays against completely random play, so that any "bad habits" and incorrect values it has formed through playing only
    #  against itself are corrected.

    loss_against_random = 0  # tracks the number of losses against random in 1000 games.
    print("now playing against random")
    last_loss = 0  # tracks the number of the last game the XOXO's lost against random
    for game in range(1, rounds_against_random):
        gamestate = 111111111
        reward = 0
        moves_against_random = []  # holds a list of moves made in the current game

        # XOXO goes first
        while True:
            gamestate = pick_move(gamestate)  # XOXO makes a move, it's recorded into the array
            moves_against_random.append(gamestate)

            if get_state_reward(gamestate) in [-1, 0.5, 1]:  # game ends
                reward += get_state_reward(gamestate)
                break

            gamestate = flip_state(gamestate)  # gameboard flips to opponent's POV

            poss_moves = list(get_my_possible_moves(gamestate).keys())
            gamestate = poss_moves[randint(0, len(poss_moves) - 1)]  # opponent plays a random move

            gamestate = flip_state(gamestate)  # gameboard flips to XOXO's POV

            if get_state_reward(gamestate) in [-1, 0.5, 1]:  # game ends
                reward += get_state_reward(gamestate)
                break
        if reward == -1:  # if the game was a loss, lowers the move values (otherwise the values are not altered anymore)
            # values of moves made are decreased, the time at which the move was played determines how significant a decrease it is.
            last_loss = (game + 1) * 1
            loss_against_random += 1
            for m in moves_against_random:
                your_moves[m] -= 0.8 ** (len(moves_against_random) - moves_against_random.index(m))

        # resets the variables for another game
        gamestate = 111111111
        reward = 0
        moves_against_random = []

        # XOXO goes second
        while True:

            poss_moves = list(get_my_possible_moves(gamestate).keys())
            gamestate = poss_moves[randint(0, len(poss_moves) - 1)]  # opponent plays a random move

            if get_state_reward(gamestate) in [-1, 0.5, 1]:  # game ends
                reward -= get_state_reward(gamestate)
                break

            gamestate = flip_state(gamestate)  # gameboard flips to XOXO's POV

            gamestate = pick_move(gamestate)  # program plays a move
            moves_against_random.append(gamestate)

            gamestate = flip_state(gamestate)  # gameboard flips to opponent's POV

            if get_state_reward(gamestate) in [-1, 0.5, 1]:  # game ends
                reward -= get_state_reward(gamestate)
                break


        if reward == -1:  # if the game was a loss, lowers the move values (otherwise the values are not altered anymore)
            # values of moves made are decreased, the time at which the move was played determines how significant a decrease it is.
            last_loss = (game + 1) * 2
            loss_against_random += 1
            for m2 in moves_against_random:
                your_moves[m2] -= 0.8 ** (len(moves_against_random) - moves_against_random.index(m2))

        if game % 500 == 0:  # keeps you informed of the number of games out of a thousand XOXO's lost to random play
            print("losses in 1000 games: " + str(loss_against_random))
            loss_against_random = 0

    print("last loss was during game " + str(last_loss))


def self_check(play_against_self):
    # here, XOXO plays against itself to check if it has really achieved unbeatable status.

    loss_against_self = 0  # this variable tracks the number of times a game has ended in something other than a loss

    print("now playing against itself")
    for game in range(play_against_self):
        gamestate = 111111111
        reward = 0
        while True:
            gamestate = pick_move(gamestate)  # 111 131 111
            if get_state_reward(gamestate) in [-1, 0.5, 1]:  # game ends
                reward += get_state_reward(gamestate)
                break

            gamestate = flip_state(gamestate)  # 111 121 111

            gamestate = pick_move(gamestate)  # 111 131 111

            gamestate = flip_state(gamestate)

            if get_state_reward(gamestate) in [-1, 0.5, 1]:  # game ends
                reward += get_state_reward(gamestate)
                break

        if reward != 0.5:
            loss_against_self += 1

    print("lost to itself " + str(loss_against_self))  # if loss_against_self isn't a 0 at this point, the code should be re-run and/or altered


all_opponent_moves = generate_moves([-1, 0])
all_moves = generate_moves([-1, 0, 1])
your_moves = generate_moves([0, 1])

first_layer()
second_layer(1500, 51)
# after multiple tests, I found 51% to be the best accuracy, giving a very slight advantage to better moves but also giving space to experimentation
third_layer(15000)  # XOXO has usually corrected all its mistakes after ~6000 or so games against random, but better be safe than sorry
third_layer(300000)




# below, the final move-value pairs are written into a text file. My Tic Tac Toe app uses this generated text file to make moves
file = open("movevalues.txt", "w")
for move in list(your_moves.keys()):  # all values are rounded to 4 decimal places
    
    # with the current version of the algorithm, move values can exceed 1 even when they're not winning moves.
    # in practice, it means XOXO can be in a situation where it can win but it will play a different move because its value is higher.
    # to correct this, all values of winning moves are set to 50 here.
    if is_win(move):
        your_moves[move] = 50.0
    
    len_of_value = len(str(your_moves[move]))
    if len_of_value > 6:
        your_moves[move] = float(str(your_moves[move])[:6])
    dot = 0
    if your_moves[move] < 0:
        len_of_value -= 1
    file.write(str(move) + "=" + str(your_moves[move]) + "0" * (6-len_of_value) + "#")

file.close()
