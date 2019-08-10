package staticanalysis.rigi;

public class UniqueArgumentAxiom extends Axiom{
	
	String txnName;
	String paramName;

	public UniqueArgumentAxiom() {
	}
	
	public UniqueArgumentAxiom(String _txnName,
			String _paramName) {
		this.txnName = _txnName;
		this.paramName = _paramName;
	} 

	@Override
	String genAxiomSpec() {
		// TODO Auto-generated method stub
		return "self.axiom = AxiomUniqueArgument(\'" + this.txnName + "\', '" + this.paramName + "\')";
	}

}
