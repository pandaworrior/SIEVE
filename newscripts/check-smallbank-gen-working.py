from z3 import *
from axioms import *
from checker import *
from table import *
from tableIns import *
from run_test import *
from argvbuilder import *

##############################################################################################

ACCOUNTS = Table('ACCOUNTS')
ACCOUNTS.addAttr('custid', Table.Type.INT)
ACCOUNTS.addAttr('name', Table.Type.STRING)
ACCOUNTS.setPKey('custid')
ACCOUNTS.setPKey('name')
ACCOUNTS.build()


SAVINGS = Table('SAVINGS')
SAVINGS.addAttr('custid', Table.Type.INT)
SAVINGS.addAttr('bal', Table.Type.REAL)
SAVINGS.setPKey('custid')
SAVINGS.build()


CHECKING = Table('CHECKING')
CHECKING.addAttr('custid', Table.Type.INT)
CHECKING.addAttr('bal', Table.Type.REAL)
CHECKING.setPKey('custid')
CHECKING.build()


def GenState():
    TABLE_ACCOUNTS = TableInstance(ACCOUNTS)
    TABLE_SAVINGS = TableInstance(SAVINGS)
    TABLE_CHECKING = TableInstance(CHECKING)
    return {'TABLE_ACCOUNTS':TABLE_ACCOUNTS,'TABLE_SAVINGS':TABLE_SAVINGS,'TABLE_CHECKING':TABLE_CHECKING}

def GenArgv():
    builder = ArgvBuilder()
    builder.NewOp('TransactSavings_run_60')
    builder.AddArgv('amount',ArgvBuilder.Type.REAL)
    builder.AddArgv('custId',ArgvBuilder.Type.INT)
    builder.AddArgv('custName',ArgvBuilder.Type.STRING)


    builder.NewOp('DepositChecking_run_55')
    builder.AddArgv('amount',ArgvBuilder.Type.REAL)
    builder.AddArgv('custId',ArgvBuilder.Type.INT)
    builder.AddArgv('custName',ArgvBuilder.Type.STRING)


    builder.NewOp('Amalgamate_run_81')
    builder.AddArgv('total',ArgvBuilder.Type.REAL)
    builder.AddArgv('custId0',ArgvBuilder.Type.INT)


    builder.NewOp('SendPayment_run_59')
    builder.AddArgv('amount',ArgvBuilder.Type.REAL)
    builder.AddArgv('sendAcct',ArgvBuilder.Type.INT)
    builder.AddArgv('destAcct',ArgvBuilder.Type.INT)


    return builder.Build()

class Op_TransactSavings_run_60():
    def __init__(self):
        self.sops = [(self.cond0, self.csop0, self.sop0)]

    def cond0(self, state, argv):
        custName = argv['TransactSavings_run_60']['custName']
        custId = argv['TransactSavings_run_60']['custId']
        custId = argv['TransactSavings_run_60']['custId']
        amount = argv['TransactSavings_run_60']['amount']
        return And((Not(state['TABLE_ACCOUNTS'].notNil({'name' : custName}) == False)),(Not(state['TABLE_ACCOUNTS'].notNil({'custid' : custId}) == False)),(Not((state['TABLE_SAVINGS'].get({'custid' : custId}, 'bal') - amount) < 0)))
    

    def csop0(self, state, argv):
        return True
    

    def sop0(self, state, argv):
        amount = argv['TransactSavings_run_60']['amount']
        custId = argv['TransactSavings_run_60']['custId']
        bal = state['TABLE_SAVINGS'].get({'custid' : custId}, 'bal')
        bal = bal - amount
        state['TABLE_SAVINGS'].update({'custid' : custId}, {'bal' : bal})
        return state




class Op_DepositChecking_run_55():
    def __init__(self):
        self.sops = [(self.cond0, self.csop0, self.sop0)]

    def cond0(self, state, argv):
        custName = argv['DepositChecking_run_55']['custName']
        amount = argv['DepositChecking_run_55']['amount']
        return And((Not(state['TABLE_ACCOUNTS'].notNil({'name' : custName}) == False)),((amount >= 0)))
    

    def csop0(self, state, argv):
        return True
    

    def sop0(self, state, argv):
        amount = argv['DepositChecking_run_55']['amount']
        custId = argv['DepositChecking_run_55']['custId']
        bal = state['TABLE_CHECKING'].get({'custid' : custId}, 'bal')
        bal = bal + amount
        state['TABLE_CHECKING'].update({'custid' : custId}, {'bal' : bal})
        return state




class Op_Amalgamate_run_81():
    def __init__(self):
        self.sops = [(self.cond0, self.csop0, self.sop0)]

    def cond0(self, state, argv):
        custId0 = argv['Amalgamate_run_81']['custId0']
        custId0 = argv['Amalgamate_run_81']['custId0']
        total = argv['Amalgamate_run_81']['total']
        custId0 = argv['Amalgamate_run_81']['custId0']
        total = argv['Amalgamate_run_81']['total']
        return And((Not(state['TABLE_ACCOUNTS'].notNil({'custid' : custId0}) == False)),(Not(state['TABLE_ACCOUNTS'].notNil({'custid' : custId0}) == False)),((total >= 0)),(((state['TABLE_CHECKING'].get({'custid' : custId0}, 'bal') - total) >= 0)))
    

    def csop0(self, state, argv):
        return True
    

    def sop0(self, state, argv):
        total = argv['Amalgamate_run_81']['total']
        custId0 = argv['Amalgamate_run_81']['custId0']
        bal = state['TABLE_SAVINGS'].get({'custid' : custId0}, 'bal')
        bal = bal + total
        state['TABLE_SAVINGS'].update({'custid' : custId0}, {'bal' : bal})
        total = argv['Amalgamate_run_81']['total']
        custId0 = argv['Amalgamate_run_81']['custId0']
        bal = state['TABLE_CHECKING'].get({'custid' : custId0}, 'bal')
        bal = bal - total
        state['TABLE_CHECKING'].update({'custid' : custId0}, {'bal' : bal})
        return state




class Op_SendPayment_run_59():
    def __init__(self):
        self.sops = [(self.cond0, self.csop0, self.sop0)]

    def cond0(self, state, argv):
        destAcct = argv['SendPayment_run_59']['destAcct']
        destAcct = argv['SendPayment_run_59']['destAcct']
        sendAcct = argv['SendPayment_run_59']['sendAcct']
        amount = argv['SendPayment_run_59']['amount']
        sendAcct = argv['SendPayment_run_59']['sendAcct']
        amount = argv['SendPayment_run_59']['amount']
        return And((Not(state['TABLE_ACCOUNTS'].notNil({'custid' : destAcct}) == False)),(Not(state['TABLE_ACCOUNTS'].notNil({'custid' : destAcct}) == False)),(Not(state['TABLE_ACCOUNTS'].notNil({'custid' : sendAcct}) == False)),((amount >= 0)),((state['TABLE_CHECKING'].get({'custid' : sendAcct}, 'bal') >= amount)))
    

    def csop0(self, state, argv):
        return True
    

    def sop0(self, state, argv):
        amount = argv['SendPayment_run_59']['amount']
        sendAcct = argv['SendPayment_run_59']['sendAcct']
        bal = state['TABLE_CHECKING'].get({'custid' : sendAcct}, 'bal')
        bal = bal - amount
        state['TABLE_CHECKING'].update({'custid' : sendAcct}, {'bal' : bal})
        amount = argv['SendPayment_run_59']['amount']
        destAcct = argv['SendPayment_run_59']['destAcct']
        bal = state['TABLE_CHECKING'].get({'custid' : destAcct}, 'bal')
        bal = bal + amount
        state['TABLE_CHECKING'].update({'custid' : destAcct}, {'bal' : bal})
        return state





class smallbank():
    def __init__(self):
        self.ops = [Op_TransactSavings_run_60(),Op_DepositChecking_run_55(),Op_Amalgamate_run_81(),Op_SendPayment_run_59()]
        self.tables = [ACCOUNTS,SAVINGS,CHECKING]
        self.state = GenState
        self.argv = GenArgv
        self.axiom = AxiomEmpty()

check(smallbank())
