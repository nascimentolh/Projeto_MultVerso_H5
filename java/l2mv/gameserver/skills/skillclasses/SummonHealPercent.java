package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.templates.StatsSet;

public class SummonHealPercent extends Skill
{
	private final boolean _ignoreHpEff;

	public SummonHealPercent(StatsSet set)
	{
		super(set);
		_ignoreHpEff = set.getBool("ignoreHpEff", true);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : targets)
		{
			if (target != null)
			{
				getEffects(activeChar, target, getActivateRate() > 0, false);

				double hp = _power * target.getMaxHp() / 100.;
				double newHp = hp * (!_ignoreHpEff ? target.calcStat(Stats.HEAL_EFFECTIVNESS, 100., activeChar, this) : 100.) / 100.;
				double addToHp = Math.max(0, Math.min(newHp, target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp() / 100. - target.getCurrentHp()));

				if (addToHp > 0)
				{
					target.setCurrentHp(addToHp + target.getCurrentHp(), false);
				}
				if (target.isPlayer())
				{
					if (activeChar != target)
					{
						target.sendPacket(new SystemMessage2(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addString(activeChar.getName()).addInteger(Math.round(addToHp)));
					}
					else
					{
						activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(addToHp)));
					}
				}
			}
		}

		if (isSSPossible())
		{
			activeChar.unChargeShots(isMagic());
		}
	}
}