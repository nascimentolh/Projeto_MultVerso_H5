package l2mv.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.database.mysql;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.serverpackets.CharacterDeleteFail;
import l2mv.gameserver.network.serverpackets.CharacterDeleteSuccess;
import l2mv.gameserver.network.serverpackets.CharacterSelectionInfo;

public class CharacterDelete extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterDelete.class);

	// cd
	private int _charSlot;

	@Override
	protected void readImpl()
	{
		this._charSlot = this.readD();
	}

	@Override
	protected void runImpl()
	{
		int clan = this.clanStatus();
		int online = this.onlineStatus();
		if (clan > 0 || online > 0)
		{
			if (clan == 2)
			{
				this.sendPacket(new CharacterDeleteFail(CharacterDeleteFail.REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED));
			}
			else if (clan == 1)
			{
				this.sendPacket(new CharacterDeleteFail(CharacterDeleteFail.REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER));
			}
			else if (online > 0)
			{
				this.sendPacket(new CharacterDeleteFail(CharacterDeleteFail.REASON_DELETION_FAILED));
			}
			return;
		}

		GameClient client = this.getClient();
		client.markToDeleteChar(this._charSlot);
		this.sendPacket(new CharacterDeleteSuccess());

		CharacterSelectionInfo cl = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
		this.sendPacket(cl);
		client.setCharSelection(cl.getCharInfo());
	}

	private int clanStatus()
	{
		int obj = this.getClient().getObjectIdForSlot(this._charSlot);
		if (obj == -1)
		{
			return 0;
		}
		if (mysql.simple_get_int("clanid", "characters", "obj_Id=" + obj) > 0)
		{
			if (mysql.simple_get_int("leader_id", "clan_subpledges", "leader_id=" + obj + " AND type = " + Clan.SUBUNIT_MAIN_CLAN) > 0)
			{
				return 2;
			}
			return 1;
		}
		return 0;
	}

	private int onlineStatus()
	{
		int obj = this.getClient().getObjectIdForSlot(this._charSlot);
		if (obj == -1)
		{
			return 0;
		}
		if (mysql.simple_get_int("online", "characters", "obj_Id=" + obj) > 0)
		{
			return 1;
		}
		return 0;
	}
}