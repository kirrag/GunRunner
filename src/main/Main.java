package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {

	static String savegames = "/home/garrik/Games/GunRunner/savegames";
	static String zipsavesfile = "saves.zip";

	public static void saveGame(int health, int weapons, int lvl, double distance, String savefile) {
		try (FileOutputStream fos = new FileOutputStream(savegames + "/" + savefile);
				ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			GameProgress gameProgress = new GameProgress(health, weapons, lvl, distance);
			oos.writeObject(gameProgress);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void packSaves() {
		File savedir = new File(savegames);

		if (savedir.isDirectory()) {
			try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(savegames + "/" + zipsavesfile))) {

				for (File savefile : savedir.listFiles()) {
					if (!savefile.isDirectory() && !(savefile.getName().equals(zipsavesfile))) {
						try (FileInputStream fis = new FileInputStream(savegames + "/" + savefile.getName())) {
							ZipEntry entry = new ZipEntry(savefile.getName());
							zout.putNextEntry(entry);
							byte[] buffer = new byte[fis.available()];
							fis.read(buffer);
							zout.write(buffer);
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}

				zout.closeEntry();

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void deleteSaves() {
		File savedir = new File(savegames);

		if (savedir.isDirectory()) {
			for (File savefile : savedir.listFiles()) {
				if (!savefile.isDirectory() && !(savefile.getName().equals(zipsavesfile))) {
					try {
						savefile.delete();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}
	}

	public static void unpackSaves() {
		try (java.util.zip.ZipInputStream zin = new ZipInputStream(
				new FileInputStream(savegames + "/" + zipsavesfile))) {
			ZipEntry entry;
			String name;
			while ((entry = zin.getNextEntry()) != null) {
				name = entry.getName();
				FileOutputStream fout = new FileOutputStream(savegames + "/" + name);
				for (int c = zin.read(); c != -1; c = zin.read()) {
					fout.write(c);
				}
				fout.flush();
				zin.closeEntry();
				fout.close();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public static void loadGame(String savefile) {
		GameProgress gameProgress = null;
		try (FileInputStream fis = new FileInputStream(savegames + "/" + savefile);
				ObjectInputStream ois = new ObjectInputStream(fis)) {
			gameProgress = (GameProgress) ois.readObject();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println(gameProgress);
	}

	public static void main(String[] args) {

		saveGame(94, 10, 2, 254.32, "save1.dat");
		saveGame(95, 11, 3, 255.33, "save2.dat");
		saveGame(96, 12, 4, 256.34, "save3.dat");
		packSaves();
		deleteSaves();
		unpackSaves();
		loadGame("save1.dat");
	}
}
