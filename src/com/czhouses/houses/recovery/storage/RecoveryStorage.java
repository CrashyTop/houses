package com.czhouses.houses.recovery.storage;

import java.util.Set;

import com.czhouses.houses.recovery.Recovery;

public interface RecoveryStorage {

	public Set<Recovery> download();

	public void add(Recovery recovery);

	public void remove(Recovery recovery);

	public void update(Recovery recovery);

}
