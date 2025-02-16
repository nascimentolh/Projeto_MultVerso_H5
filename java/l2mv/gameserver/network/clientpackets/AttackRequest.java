package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

@SuppressWarnings("unused")
public class AttackRequest extends L2GameClientPacket
{
	private int _objectId;
	private int _originX;
	private int _originY;
	private int _originZ;
	private int _attackId;

	@Override
	protected void readImpl()
	{
		this._objectId = this.readD();
		this._originX = this.readD();
		this._originY = this.readD();
		this._originZ = this.readD();
		this._attackId = this.readC();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		activeChar.setActive();

		if (activeChar.isOutOfControl() || !activeChar.getPlayerAccess().CanAttack)
		{
			activeChar.sendActionFailed();
			return;
		}

		GameObject target = activeChar.getVisibleObject(this._objectId);
		if ((target == null) || activeChar.isPendingOlyEnd())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.getAggressionTarget() != null && activeChar.getAggressionTarget() != target && !activeChar.getAggressionTarget().isDead())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (target.isPlayer() && (activeChar.isInBoat() || target.isInBoat()))
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.getTarget() != target)
		{
			if (activeChar.isLockedTarget())
			{
				if (activeChar.isClanAirShipDriver())
				{
					activeChar.sendPacket(SystemMsg.THIS_ACTION_IS_PROHIBITED_WHILE_STEERING);
				}

				activeChar.sendActionFailed();
				return;
			}

			target.onAction(activeChar, false);
			return;
		}

		if (target.getObjectId() != activeChar.getObjectId() && !activeChar.isInStoreMode() && !activeChar.isProcessingRequest())
		{
			target.onForcedAttack(activeChar, false);
		}
	}
}