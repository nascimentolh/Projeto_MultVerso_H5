package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.network.serverpackets.ExShowAgitInfo;

public class RequestAllAgitInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		this.getClient().getActiveChar().sendPacket(new ExShowAgitInfo());
	}
}