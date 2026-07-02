import os, random

split_list = False
ships_per_split = 50


def main():
    # Initialize Necessary Variables
    ship_count = 0
    ship_list = os.listdir(".")

    #if split_list:
    #    random.shuffle(ship_list)
    
    for f in ship_list:
        if split_list:
            if ship_count % ships_per_split == 0:
                print() # insert a blank line
                print() # insert a blank line

        if f != '_list_files.py':
            ship_count += 1
            #print(str(ship_count), f)
            print(f)

    print("=====================================================================")
    print("Listed", str(ship_count), "ships.")


if __name__ == "__main__":
    main()
