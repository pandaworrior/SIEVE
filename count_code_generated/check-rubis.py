from z3 import *

def gen_id(total = []):
    m = 0
    if total:
        m = total[-1]
    total.append(m + 1)
    return str(total[-1])

debug = True

def Implies2(P,Q):
    return And(Implies(P,Q),Implies(Q,P))

def z3_list_eq(X,Y):
    X_eq_Y = [ X[i] == Y[i] for i in range(len(X)) ]
    return simplify(And(X_eq_Y)) 

##############################################################################################

ID = IntSort()

winners = Datatype('winners')
winners.declare('new',('winner_id', IntSort()),('item_id', IntSort()),('bid', RealSort()))
winners = winners.create()

buy_now = Datatype('buy_now')
buy_now.declare('new',('buyer_id', IntSort()),('item_id', IntSort()),('qty', IntSort()),('date', IntSort()))
buy_now = buy_now.create()

regions = Datatype('regions')
regions.declare('new',('name', StringSort()))
regions = regions.create()

comments = Datatype('comments')
comments.declare('new',('from_user_id', IntSort()),('to_user_id', IntSort()),('item_id', IntSort()),('rating', IntSort()),('date', IntSort()),('comment', StringSort()))
comments = comments.create()

bids = Datatype('bids')
bids.declare('new',('user_id', IntSort()),('item_id', IntSort()),('qty', IntSort()),('bid', RealSort()),('max_bid', RealSort()),('date', IntSort()))
bids = bids.create()

categories = Datatype('categories')
categories.declare('new',('name', StringSort()))
categories = categories.create()

items = Datatype('items')
items.declare('new',('name', StringSort()),('description', StringSort()),('initial_price', RealSort()),('quantity', IntSort()),('reserve_price', RealSort()),('buy_now', RealSort()),('nb_of_bids', IntSort()),('max_bid', RealSort()),('start_date', IntSort()),('end_date', IntSort()),('seller', IntSort()),('category', IntSort()),('_SP_clock', StringSort()))
items = items.create()

users = Datatype('users')
users.declare('new',('firstname', StringSort()),('lastname', StringSort()),('nickname', StringSort()),('password', StringSort()),('email', StringSort()),('rating', IntSort()),('balance', RealSort()),('creation_date', IntSort()),('region', IntSort()),('_SP_clock', StringSort()))
users = users.create()

def GenState(label):
    table_winners = Array('tab0' + label + gen_id(), ID, winners)
    table_buy_now = Array('tab1' + label + gen_id(), ID, buy_now)
    table_regions = Array('tab2' + label + gen_id(), ID, regions)
    table_comments = Array('tab3' + label + gen_id(), ID, comments)
    table_bids = Array('tab4' + label + gen_id(), ID, bids)
    table_categories = Array('tab5' + label + gen_id(), ID, categories)
    table_items = Array('tab6' + label + gen_id(), ID, items)
    table_users = Array('tab7' + label + gen_id(), ID, users)
    return [table_winners,table_buy_now,table_regions,table_comments,table_bids,table_categories,table_items,table_users]


##############################################################################################

def GetItemByUserAndDate(m, id): 
    rec_items = Select(m, id)
    return rec_items

def GetItemByUser(m, id): 
    rec_items = Select(m, id)
    return rec_items

def GetItemById(m, id): 
    rec_items = Select(m, id)
    return rec_items

def GetNickName(m, id): 
    rec_users = Select(m, id)
    f_rec_nickname = users.nickname(rec_users)
    return [f_rec_nickname]


def GetRegion(m, id): 
    rec_regions = Select(m, id)
    f_rec_id = regions.id(rec_regions)
    return [f_rec_id]


def GetRegionNameId(m, id): 
    rec_regions = Select(m, id)
    f_rec_name = regions.name(rec_regions)
    f_rec_id = regions.id(rec_regions)
    return [f_rec_name, f_rec_id]




def UpdateItem1(m, id, end_date_p): 
    o_rec_items = Select(m, id)
    o_f_rec_end_date = items.end_date(rec_items)
    n_f_rec_end_date = end_date_p
    n_rec_items = items.new(n_f_rec_end_date)
    return Store(m, id, n_rec_items)






def SearchItemByCategorty(m, id): 
    rec_items = Select(m, id)
    f_rec_name = items.name(rec_items)
    f_rec_id = items.id(rec_items)
    f_rec_end_date = items.end_date(rec_items)
    f_rec_max_bid = items.max_bid(rec_items)
    f_rec_nb_of_bids = items.nb_of_bids(rec_items)
    f_rec_initial_price = items.initial_price(rec_items)
    return [f_rec_name, f_rec_id, f_rec_end_date, f_rec_max_bid, f_rec_nb_of_bids, f_rec_initial_price]




def UpdateItem(m, id, end_date_p, quantity_p): 
    o_rec_items = Select(m, id)
    o_f_rec_end_date = items.end_date(rec_items)
    n_f_rec_end_date = end_date_p
    o_f_rec_quantity = items.quantity(rec_items)
    n_f_rec_quantity = quantity_p
    n_rec_items = items.new(n_f_rec_end_date, n_f_rec_quantity)
    return Store(m, id, n_rec_items)




def ViewBid(m, id): 
    rec_bids = Select(m, id)
    return rec_bids

def ReadComment(m, id): 
    rec_comments = Select(m, id)
    return rec_comments


##############################################################################################


##############################################################################################

class RegisterUser-doGet-45(object):
    def __init__(self):
        self.ops = ([(self.cond0, self.sop0), (self.cond1, self.sop1), (self.cond2, self.sop2), (self.cond3, self.sop3), (self.cond4, self.sop4), (self.cond5, self.sop5), (self.cond6, self.sop6), (self.cond7, self.sop7), (self.cond8, self.sop8), (self.cond9, self.sop9), (self.cond10, self.sop10), (self.cond11, self.sop11), (self.cond12, self.sop12), (self.cond13, self.sop13)])
        self.sync = set([])
        self.depend = set([])
        self.write = set([])

    def cond11(self, state, argv):
        return And()

    def sop11(self, state, argv):
        o_rec_users = Select(table_users, id)
        o_f_rec_id = users.id(rec_users)
        n_f_rec_id = id_p
        o_f_rec_firstname = users.firstname(rec_users)
        n_f_rec_firstname = firstname_p
        o_f_rec_lastname = users.lastname(rec_users)
        n_f_rec_lastname = lastname_p
        o_f_rec_nickname = users.nickname(rec_users)
        n_f_rec_nickname = nickname_p
        o_f_rec_password = users.password(rec_users)
        n_f_rec_password = password_p
        o_f_rec_email = users.email(rec_users)
        n_f_rec_email = email_p
        o_f_rec_rating = users.rating(rec_users)
        n_f_rec_rating = rating_p
        o_f_rec_balance = users.balance(rec_users)
        n_f_rec_balance = balance_p
        o_f_rec_creation_date = users.creation_date(rec_users)
        n_f_rec_creation_date = creation_date_p
        o_f_rec_region = users.region(rec_users)
        n_f_rec_region = region_p
        n_rec_users = users.new(n_f_rec_id, n_f_rec_firstname, n_f_rec_lastname, n_f_rec_nickname, n_f_rec_password, n_f_rec_email, n_f_rec_rating, n_f_rec_balance, n_f_rec_creation_date, n_f_rec_region)
        table_users_0 = Store(table_users, id, n_rec_users)
        state_0 = [table_winners, table_buy_now, table_regions, table_comments, table_bids, table_categories, table_items, table_users_1]
        return state_0



class StoreBuyNow-doPost-77(object):
    def __init__(self):
        self.ops = ([(self.cond0, self.sop0), (self.cond1, self.sop1), (self.cond2, self.sop2), (self.cond3, self.sop3), (self.cond4, self.sop4), (self.cond5, self.sop5), (self.cond6, self.sop6), (self.cond7, self.sop7), (self.cond8, self.sop8), (self.cond9, self.sop9), (self.cond10, self.sop10), (self.cond11, self.sop11), (self.cond12, self.sop12)])
        self.sync = set([])
        self.depend = set([])
        self.write = set([])

    def cond9(self, state, argv):
        return And()

    def sop9(self, state, argv):
        o_rec_items = Select(table_items, id)
        o_f_rec_quantity = items.quantity(rec_items)
        n_f_rec_quantity = quantity_p
        n_rec_items = items.new(n_f_rec_quantity)
        table_items_0 = Store(table_items, id, n_rec_items)
        o_rec_buy_now = Select(table_buy_now, id)
        o_f_rec_id = buy_now.id(rec_buy_now)
        n_f_rec_id = id_p
        o_f_rec_buyer_id = buy_now.buyer_id(rec_buy_now)
        n_f_rec_buyer_id = buyer_id_p
        o_f_rec_item_id = buy_now.item_id(rec_buy_now)
        n_f_rec_item_id = item_id_p
        o_f_rec_qty = buy_now.qty(rec_buy_now)
        n_f_rec_qty = qty_p
        o_f_rec_date = buy_now.date(rec_buy_now)
        n_f_rec_date = date_p
        n_rec_buy_now = buy_now.new(n_f_rec_id, n_f_rec_buyer_id, n_f_rec_item_id, n_f_rec_qty, n_f_rec_date)
        table_buy_now_0 = Store(table_buy_now, id, n_rec_buy_now)
        state_0 = [table_winners, table_buy_now_1, table_regions, table_comments, table_bids, table_categories, table_items_1, table_users]
        return state_0

    def cond10(self, state, argv):
        return And()

    def sop10(self, state, argv):
        o_rec_items = Select(table_items, id)
        o_f_rec_quantity = items.quantity(rec_items)
        n_f_rec_quantity = quantity_p
        n_rec_items = items.new(n_f_rec_quantity)
        table_items_0 = Store(table_items, id, n_rec_items)
        o_rec_buy_now = Select(table_buy_now, id)
        o_f_rec_id = buy_now.id(rec_buy_now)
        n_f_rec_id = id_p
        o_f_rec_buyer_id = buy_now.buyer_id(rec_buy_now)
        n_f_rec_buyer_id = buyer_id_p
        o_f_rec_item_id = buy_now.item_id(rec_buy_now)
        n_f_rec_item_id = item_id_p
        o_f_rec_qty = buy_now.qty(rec_buy_now)
        n_f_rec_qty = qty_p
        o_f_rec_date = buy_now.date(rec_buy_now)
        n_f_rec_date = date_p
        n_rec_buy_now = buy_now.new(n_f_rec_id, n_f_rec_buyer_id, n_f_rec_item_id, n_f_rec_qty, n_f_rec_date)
        table_buy_now_0 = Store(table_buy_now, id, n_rec_buy_now)
        state_0 = [table_winners, table_buy_now_1, table_regions, table_comments, table_bids, table_categories, table_items_1, table_users]
        return state_0


class StoreBid-doPost-74(object):
    def __init__(self):
        self.ops = ([(self.cond0, self.sop0), (self.cond1, self.sop1), (self.cond2, self.sop2), (self.cond3, self.sop3), (self.cond4, self.sop4), (self.cond5, self.sop5), (self.cond6, self.sop6), (self.cond7, self.sop7), (self.cond8, self.sop8), (self.cond9, self.sop9), (self.cond10, self.sop10), (self.cond11, self.sop11), (self.cond12, self.sop12), (self.cond13, self.sop13), (self.cond14, self.sop14), (self.cond15, self.sop15), (self.cond16, self.sop16)])
        self.sync = set([])
        self.depend = set([])
        self.write = set([])

    def cond12(self, state, argv):
        return And()

    def sop12(self, state, argv):
        o_rec_bids = Select(table_bids, id)
        o_f_rec_id = bids.id(rec_bids)
        n_f_rec_id = id_p
        o_f_rec_user_id = bids.user_id(rec_bids)
        n_f_rec_user_id = user_id_p
        o_f_rec_item_id = bids.item_id(rec_bids)
        n_f_rec_item_id = item_id_p
        o_f_rec_qty = bids.qty(rec_bids)
        n_f_rec_qty = qty_p
        o_f_rec_bid = bids.bid(rec_bids)
        n_f_rec_bid = bid_p
        o_f_rec_max_bid = bids.max_bid(rec_bids)
        n_f_rec_max_bid = max_bid_p
        o_f_rec_date = bids.date(rec_bids)
        n_f_rec_date = date_p
        n_rec_bids = bids.new(n_f_rec_id, n_f_rec_user_id, n_f_rec_item_id, n_f_rec_qty, n_f_rec_bid, n_f_rec_max_bid, n_f_rec_date)
        table_bids_0 = Store(table_bids, id, n_rec_bids)
        state_0 = [table_winners, table_buy_now, table_regions, table_comments, table_bids_1, table_categories, table_items, table_users]
        return state_0

    def cond13(self, state, argv):
        return And()

    def sop13(self, state, argv):
        o_rec_bids = Select(table_bids, id)
        o_f_rec_id = bids.id(rec_bids)
        n_f_rec_id = id_p
        o_f_rec_user_id = bids.user_id(rec_bids)
        n_f_rec_user_id = user_id_p
        o_f_rec_item_id = bids.item_id(rec_bids)
        n_f_rec_item_id = item_id_p
        o_f_rec_qty = bids.qty(rec_bids)
        n_f_rec_qty = qty_p
        o_f_rec_bid = bids.bid(rec_bids)
        n_f_rec_bid = bid_p
        o_f_rec_max_bid = bids.max_bid(rec_bids)
        n_f_rec_max_bid = max_bid_p
        o_f_rec_date = bids.date(rec_bids)
        n_f_rec_date = date_p
        n_rec_bids = bids.new(n_f_rec_id, n_f_rec_user_id, n_f_rec_item_id, n_f_rec_qty, n_f_rec_bid, n_f_rec_max_bid, n_f_rec_date)
        table_bids_0 = Store(table_bids, id, n_rec_bids)
        state_0 = [table_winners, table_buy_now, table_regions, table_comments, table_bids_1, table_categories, table_items, table_users]
        return state_0

    def cond14(self, state, argv):
        return And()

    def sop14(self, state, argv):
        o_rec_bids = Select(table_bids, id)
        o_f_rec_id = bids.id(rec_bids)
        n_f_rec_id = id_p
        o_f_rec_user_id = bids.user_id(rec_bids)
        n_f_rec_user_id = user_id_p
        o_f_rec_item_id = bids.item_id(rec_bids)
        n_f_rec_item_id = item_id_p
        o_f_rec_qty = bids.qty(rec_bids)
        n_f_rec_qty = qty_p
        o_f_rec_bid = bids.bid(rec_bids)
        n_f_rec_bid = bid_p
        o_f_rec_max_bid = bids.max_bid(rec_bids)
        n_f_rec_max_bid = max_bid_p
        o_f_rec_date = bids.date(rec_bids)
        n_f_rec_date = date_p
        n_rec_bids = bids.new(n_f_rec_id, n_f_rec_user_id, n_f_rec_item_id, n_f_rec_qty, n_f_rec_bid, n_f_rec_max_bid, n_f_rec_date)
        table_bids_0 = Store(table_bids, id, n_rec_bids)
        state_0 = [table_winners, table_buy_now, table_regions, table_comments, table_bids_1, table_categories, table_items, table_users]
        return state_0

    def cond15(self, state, argv):
        return And()

    def sop15(self, state, argv):
        o_rec_bids = Select(table_bids, id)
        o_f_rec_id = bids.id(rec_bids)
        n_f_rec_id = id_p
        o_f_rec_user_id = bids.user_id(rec_bids)
        n_f_rec_user_id = user_id_p
        o_f_rec_item_id = bids.item_id(rec_bids)
        n_f_rec_item_id = item_id_p
        o_f_rec_qty = bids.qty(rec_bids)
        n_f_rec_qty = qty_p
        o_f_rec_bid = bids.bid(rec_bids)
        n_f_rec_bid = bid_p
        o_f_rec_max_bid = bids.max_bid(rec_bids)
        n_f_rec_max_bid = max_bid_p
        o_f_rec_date = bids.date(rec_bids)
        n_f_rec_date = date_p
        n_rec_bids = bids.new(n_f_rec_id, n_f_rec_user_id, n_f_rec_item_id, n_f_rec_qty, n_f_rec_bid, n_f_rec_max_bid, n_f_rec_date)
        table_bids_0 = Store(table_bids, id, n_rec_bids)
        o_rec_items = Select(table_items, id)
        o_f_rec_nb_of_bids = items.nb_of_bids(rec_items)
        n_f_rec_nb_of_bids = nb_of_bids_p
        n_rec_items = items.new(n_f_rec_nb_of_bids)
        table_items_0 = Store(table_items, id, n_rec_items)
        state_0 = [table_winners, table_buy_now, table_regions, table_comments, table_bids_1, table_categories, table_items_1, table_users]
        return state_0


class RegisterItem-doGet-44(object):
    def __init__(self):
        self.ops = ([(self.cond0, self.sop0)])
        self.sync = set([])
        self.depend = set([])
        self.write = set([])

    def cond27(self, state, argv):
        return And()

    def sop27(self, state, argv):
        o_rec_items = Select(table_items, id)
        o_f_rec_id = items.id(rec_items)
        n_f_rec_id = id_p
        o_f_rec_name = items.name(rec_items)
        n_f_rec_name = name_p
        o_f_rec_description = items.description(rec_items)
        n_f_rec_description = description_p
        o_f_rec_initial_price = items.initial_price(rec_items)
        n_f_rec_initial_price = initial_price_p
        o_f_rec_quantity = items.quantity(rec_items)
        n_f_rec_quantity = quantity_p
        o_f_rec_reserve_price = items.reserve_price(rec_items)
        n_f_rec_reserve_price = reserve_price_p
        o_f_rec_buy_now = items.buy_now(rec_items)
        n_f_rec_buy_now = buy_now_p
        o_f_rec_nb_of_bids = items.nb_of_bids(rec_items)
        n_f_rec_nb_of_bids = nb_of_bids_p
        o_f_rec_max_bid = items.max_bid(rec_items)
        n_f_rec_max_bid = max_bid_p
        o_f_rec_start_date = items.start_date(rec_items)
        n_f_rec_start_date = start_date_p
        o_f_rec_end_date = items.end_date(rec_items)
        n_f_rec_end_date = end_date_p
        o_f_rec_seller = items.seller(rec_items)
        n_f_rec_seller = seller_p
        o_f_rec_category = items.category(rec_items)
        n_f_rec_category = category_p
        n_rec_items = items.new(n_f_rec_id, n_f_rec_name, n_f_rec_description, n_f_rec_initial_price, n_f_rec_quantity, n_f_rec_reserve_price, n_f_rec_buy_now, n_f_rec_nb_of_bids, n_f_rec_max_bid, n_f_rec_start_date, n_f_rec_end_date, n_f_rec_seller, n_f_rec_category)
        table_items_0 = Store(table_items, id, n_rec_items)
        state_0 = [table_winners, table_buy_now, table_regions, table_comments, table_bids, table_categories, table_items_1, table_users]
        return state_0


class StoreComment-doPost-60(object):
    def __init__(self):
        self.ops = ([(self.cond0, self.sop0), (self.cond1, self.sop1), (self.cond2, self.sop2), (self.cond3, self.sop3), (self.cond4, self.sop4), (self.cond5, self.sop5), (self.cond6, self.sop6), (self.cond7, self.sop7), (self.cond8, self.sop8), (self.cond9, self.sop9), (self.cond10, self.sop10)])
        self.sync = set([])
        self.depend = set([])
        self.write = set([])

    def cond6(self, state, argv):
        return And()

    def sop6(self, state, argv):
        o_rec_comments = Select(table_comments, id)
        o_f_rec_id = comments.id(rec_comments)
        n_f_rec_id = id_p
        o_f_rec_from_user_id = comments.from_user_id(rec_comments)
        n_f_rec_from_user_id = from_user_id_p
        o_f_rec_to_user_id = comments.to_user_id(rec_comments)
        n_f_rec_to_user_id = to_user_id_p
        o_f_rec_item_id = comments.item_id(rec_comments)
        n_f_rec_item_id = item_id_p
        o_f_rec_rating = comments.rating(rec_comments)
        n_f_rec_rating = rating_p
        o_f_rec_date = comments.date(rec_comments)
        n_f_rec_date = date_p
        o_f_rec_comment = comments.comment(rec_comments)
        n_f_rec_comment = comment_p
        n_rec_comments = comments.new(n_f_rec_id, n_f_rec_from_user_id, n_f_rec_to_user_id, n_f_rec_item_id, n_f_rec_rating, n_f_rec_date, n_f_rec_comment)
        table_comments_0 = Store(table_comments, id, n_rec_comments)
        o_rec_users = Select(table_users, id)
        o_f_rec_rating = users.rating(rec_users)
        n_f_rec_rating = rating_p
        n_rec_users = users.new(n_f_rec_rating)
        table_users_0 = Store(table_users, id, n_rec_users)
        state_0 = [table_winners, table_buy_now, table_regions, table_comments_1, table_bids, table_categories, table_items, table_users_1]
        return state_0

    def cond7(self, state, argv):
        return And()

    def sop7(self, state, argv):
        o_rec_comments = Select(table_comments, id)
        o_f_rec_id = comments.id(rec_comments)
        n_f_rec_id = id_p
        o_f_rec_from_user_id = comments.from_user_id(rec_comments)
        n_f_rec_from_user_id = from_user_id_p
        o_f_rec_to_user_id = comments.to_user_id(rec_comments)
        n_f_rec_to_user_id = to_user_id_p
        o_f_rec_item_id = comments.item_id(rec_comments)
        n_f_rec_item_id = item_id_p
        o_f_rec_rating = comments.rating(rec_comments)
        n_f_rec_rating = rating_p
        o_f_rec_date = comments.date(rec_comments)
        n_f_rec_date = date_p
        o_f_rec_comment = comments.comment(rec_comments)
        n_f_rec_comment = comment_p
        n_rec_comments = comments.new(n_f_rec_id, n_f_rec_from_user_id, n_f_rec_to_user_id, n_f_rec_item_id, n_f_rec_rating, n_f_rec_date, n_f_rec_comment)
        table_comments_0 = Store(table_comments, id, n_rec_comments)
        state_0 = [table_winners, table_buy_now, table_regions, table_comments_1, table_bids, table_categories, table_items, table_users]
        return state_0



class CloseAuction-doGet-40(object):
    def __init__(self):
        self.ops = ([(self.cond0, self.sop0), (self.cond1, self.sop1), (self.cond2, self.sop2), (self.cond3, self.sop3)])
        self.sync = set([])
        self.depend = set([])
        self.write = set([])

    def cond2(self, state, argv):
        return And()

    def sop2(self, state, argv):
        o_rec_winners = Select(table_winners, )
        o_f_rec_winner_id = winners.winner_id(rec_winners)
        n_f_rec_winner_id = winner_id_p
        o_f_rec_item_id = winners.item_id(rec_winners)
        n_f_rec_item_id = item_id_p
        o_f_rec_bid = winners.bid(rec_winners)
        n_f_rec_bid = bid_p
        n_rec_winners = winners.new(n_f_rec_winner_id, n_f_rec_item_id, n_f_rec_bid)
        table_winners_0 = Store(table_winners, , n_rec_winners)
        o_rec_items = Select(table_items, id)
        o_f_rec_end_date = items.end_date(rec_items)
        n_f_rec_end_date = end_date_p
        n_rec_items = items.new(n_f_rec_end_date)
        table_items_0 = Store(table_items, id, n_rec_items)
        state_0 = [table_winners_1, table_buy_now, table_regions, table_comments, table_bids, table_categories, table_items_1, table_users]
        return state_0


op_list = [RegisterUser-doGet-45(), StoreBuyNow-doPost-77(), StoreBid-doPost-74(), RegisterItem-doGet-44(), StoreComment-doPost-60(), CloseAuction-doGet-40()]

##############################################################################################

def Self_Free(f,c,I):
    state = GenState("")
    argv = GenArgv("")
    return ForAll(list(state)+list(argv), Implies(And(I(state),c(state,argv)),I(f(state,argv))))

def State_Free(f1,c1,f2,c2,I):
    stateA = GenState("A")
    argvA = GenArgv("A")
    stateB = GenState("B")
    argvB = GenArgv("B")
    state = GenState("")
    return ForAll(list(stateA)+list(argvA)+list(stateB)+list(argvB)+list(state),
                Implies(And(
                    c1(stateA,argvA),c2(stateB,argvB)
                    #,c1(f2(state,argvB),argvA),c2(f1(state,argvA),argvB)
                    )
                ,z3_list_eq(f2(f1(state,argvA),argvB),
                           f1(f2(state,argvB),argvA))))


def State_Free_Inv(f1,c1,f2,c2,I):
    stateA = GenState("A")
    argvA = GenArgv("A")
    stateB = GenState("B")
    argvB = GenArgv("B")
    state = GenState("")
    return ForAll(list(stateA)+list(argvA)+list(stateB)+list(argvB)+list(state),
                Implies(And(
                    c1(stateA,argvA), c2(f1(stateA,argvA),argvB)
                    )
                ,z3_list_eq(f2(f1(state,argvA),argvB),
                           f1(f2(state,argvB),argvA))))

def Invariant_Free(f1,c1,f2,c2,I):
    stateA = GenState("A")
    argvA = GenArgv("A")
    stateB = GenState("B")
    argvB = GenArgv("B")
    state = GenState("")
    return ForAll(list(stateA)+list(argvA)+list(stateB)+list(argvB)+list(state),
            Implies(And(c1(stateA,argvA),c2(stateB,argvB),c2(f1(state,argvA),argvB),I(state),I(f1(state,argvA)),I(f2(f1(state,argvA),argvB))),
                And(I(f2(state,argvB)),I(f1(f2(state,argvB),argvA)))))


def equal(s1,s2,c):
    argv = GenArgv("")
    argv_ = GenArgv("_")
    return Or(Implies(c(s1,argv),c(s2,argv_)),Implies(c(s1,argv),c(s2,argv)))

def Context_Free(f1,c1,f2,c2,I):
    state = GenState("")
    argvA = GenArgv("A")
    argvB = GenArgv("B")
    argvB_ = GenArgv("B_")
    return ForAll(list(argvA)+list(state), Implies(And(c1(state,argvA),I(state),I(f1(state,argvA))),equal(state,f1(state,argvA),c2))) 

def isFree2(F,op,I):
    for c,f in op.ops:
        solver = Solver()
        solver.add(Not(F(f,c,I)))
        if solver.check() == sat:
            return False
        elif solver.check() == unknown:
            print "unknown!!!"
            exit()
    return True

def isFree(F,op1,op2,I):
    for c2,f2 in op2.ops:
        for c1,f1 in op1.ops:
            solver = Solver()
            solver.add(Not(F(f1,c1,f2,c2,I)))
            if solver.check() == sat:
                return False
            elif solver.check() == unknown:
                print "unknown!!!"
                exit()
    return True
    
def sync(op1, op2):
    return (len(op1.write & op2.sync)) != 0

def depend(op1, op2):
    return (len(op1.write & op2.depend)) != 0

def SAT_DEBUG():
    for op in op_list:
        print "check " + op.__class__.__name__
        if not isFree2(Self_Free,op,Inv):
            print op.__class__.__name__ + " not sat"
            # return False

    for op1 in op_list:
        for op2 in op_list:
            print "check " + op2.__class__.__name__ + " and " + op1.__class__.__name__
            if not ((sync(op1, op2) and depend(op1, op2)) or isFree(State_Free,op1,op2,Inv) or (sync(op1, op2) and isFree(State_Free_Inv,op1,op2,Inv))):
                print op2.__class__.__name__ + " is not state-free of " + op1.__class__.__name__
                # return False
            if not (sync(op1, op2) or isFree(Context_Free,op1,op2,Inv)):
                print op2.__class__.__name__ + " is not context-free of " + op1.__class__.__name__
                # return False
            if not (depend(op1, op2) or isFree(Invariant_Free,op1,op2,Inv)):
                print op2.__class__.__name__ + " is not invariant-free of " + op1.__class__.__name__
                # return False
    return True

def SAT():
    for op1 in op_list:
        for op2 in op_list:
            if not ((isFree(State_Free,op1,op2,Inv) or (sync(op1, op2) and depend(op1, op2))) and
                  (isFree(Context_Free,op1,op2,Inv) or sync(op1, op2)) and
                  (isFree(Invariant_Free,op1,op2,Inv) or depend(op1, op2))):
                return False
    return True

print SAT_DEBUG()
