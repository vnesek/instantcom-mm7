package net.instantcom.mm7;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * sample VASP to use with {@link MM7ServletInJettyWithSpring}
 * </p>
 * Created by bardug on 6/26/2016.
 */
public class SampleSpringVASP implements VASP {

    @Autowired
    private MM7Context context;

    @Override
    public DeliverRsp deliver(DeliverReq deliverReq) throws MM7Error {
        System.out.println("deliver in VASP was called");

        return null;
    }

    @Override
    public MM7Context getContext() {
        return context;
    }

	@Override
	public DeliveryReportRsp deliveryReport(DeliveryReportReq deliveryReportReq) throws MM7Error {
		System.out.println("deliveryReport in VASP was called");

        return null;
	}

	@Override
	public ReadReplyRsp readReply(ReadReplyReq readReplyReq) throws MM7Error {
		System.out.println("readReplyReq in VASP was called");
		
		return null;
	}
}
